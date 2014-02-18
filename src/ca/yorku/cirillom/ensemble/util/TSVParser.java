package ca.yorku.cirillom.ensemble.util;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.PerformanceData;
import ca.yorku.cirillom.ensemble.ui.util.ProgressWindow;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 11:40 PM.
 * <p/>
 * Description
 * <p/>
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 */
public class TSVParser extends SwingWorker<Boolean, Integer>  {

    private final File file;
    private final ProgressWindow progress;
    private List<PerformanceData> list;

    public TSVParser(File file, ProgressWindow progress) {
        super();

        this.file = file;
        this.progress = progress;
    }

    protected void process(final List<Integer> chunks) {
        for (final int p : chunks) {
            progress.set(p);    // update progress
        }
    }

    protected Boolean doInBackground() throws InterruptedException {

        // Get current line number and total line numbers so we can update the progress
        int currentLine  = 0;
        int fileSize     = Util.getFileLength(file);

        // This map will make it easier to keep track of which DataValues belong to which PerformanceDatas
        Map<String, PerformanceData> perfData = new HashMap<>();

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {

            String line = in.readLine();
            List<String> processes  = getProcesses(line);
            List<String> metrics    = getMetrics(line);

            // Increment progress
            publish(percent(++currentLine, fileSize));

            // Fill process map
            for (String process : processes) {
                perfData.put(process, new PerformanceData(process));
            }

            while( null != (line = in.readLine()) ) {

                String[] elems = Util.replace(line.split("\t"), "\"", "");  // split and remove quotes

                // parse Date
                Date time;
                try {
                    DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
                    time = format.parse(elems[0]);
                } catch (ParseException pex) {
                    DateFormat format = new SimpleDateFormat("HH:mm.S");
                    time = format.parse(elems[0]);
                }


                // Parse DataValues
                for (int i = 1; i < elems.length; i++) {

                    // try to get raw value
                    double value = 0;
                    try {
                        value = Double.parseDouble(elems[i]);
                    } catch (NumberFormatException e) { }

                    PerformanceData p = perfData.get(processes.get(i-1));   // i-1 because processes is 0-indexed
                    p.add(metrics.get(i-1), new DataValue(value, time));    // add the value
                }

                // Increment progress
                publish(percent(++currentLine, fileSize));
            }

        } catch (ParseException | IOException ex) {
            ex.printStackTrace();
        }

        // Create List for return
        this.list = new ArrayList<>();
        for (Map.Entry<String, PerformanceData> p : perfData.entrySet()) {
           list.add(p.getValue());
        }

        this.firePropertyChange("finished", null, true);

        // close ProgressWindow
        progress.dispose();

        return true;
        //return result;
    }

    /**
     *
     * @param firstLine - the first line of a TSV file
     * @return Process names, in a list
     */
    private static List<String> getProcesses(String firstLine) {

        List<String> result = new ArrayList<String>();

        // First Line Example
        // "(PDH-TSV 4.0) (Eastern Daylight Time)(240)"	"\\LT501BES02\Process(mysqld)\% Processor Time"
        String[] elems = Util.replace(firstLine.split("\t"), "\"", ""); // split and remove quotes

        for (String s : elems) {

            if (s.startsWith("\\\\")) {
            // Starts with `\\` as in \\LT501BES02

                // process is the next field after the next `\`
                // Process name continues until hitting the last `\`
                String process = s.substring(s.indexOf("\\", 2) + 1, s.lastIndexOf('\\'));
                result.add(process);
            }
        }

        return result;
    }

    public List<PerformanceData> getResult() {
        return this.list;
    }

    /**
     *
     * @param firstLine - the first line of a TSV file
     * @return metric names, in a list
     */
    private static List<String> getMetrics(String firstLine) {

        List<String> result = new ArrayList<String>();

        // First Line Example
        // "(PDH-TSV 4.0) (Eastern Daylight Time)(240)"	"\\LT501BES02\Process(mysqld)\% Processor Time"
        String[] elems = Util.replace(firstLine.split("\t"), "\"", ""); // split and remove quotes

        for (String s : elems) {

            if (s.startsWith("\\\\")) {
            // Starts with `\\` as in \\LT501BES02

                // metrics are the last `\`-delimited field
                String metric = s.substring(s.lastIndexOf('\\') + 1);

                result.add(metric);
            }
        }

        return result;
    }

    private int percent(int val, int maxVal) {
        double a = val;
        double b = maxVal;
        double result = (a/b) * 100.0;

        return (int) result;
    }
}
