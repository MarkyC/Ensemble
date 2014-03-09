package ca.yorku.cirillom.ensemble.util;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.Metric;
import ca.yorku.cirillom.ensemble.models.PerformanceData;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class XMLParser extends SwingWorker<PerformanceData, Integer> {

    private final File file;
    private List<PropertyChangeListener> listeners = new ArrayList<>();

    private PerformanceData result;
    public PerformanceData getResult() {
        return result;
    }

    public XMLParser(File file) {
        super();
        this.file = file;
    }

    protected PerformanceData doInBackground() throws InterruptedException {

        List<DataValue> dataValues = new ArrayList<>();

        try {

            Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            xml.getDocumentElement().normalize();   // Normalize to prevent bugs

            int numMetrics      = 0; // Number of Metrics Processed
            int totalMetrics    = xml.getElementsByTagName("Metric").getLength(); // Total Number of Metrics

            NodeList dataValueNodes = xml.getElementsByTagName("DataValue");

            for (int i = 0; i < dataValueNodes.getLength();i++) {

                // get item for this iteration
                Node dataValueNode = dataValueNodes.item(i);

                // Child Nodes should be Metrics
                NodeList metricNodes = dataValueNode.getChildNodes();

                // Fill Metric List for this DataValue
                List<Metric> metrics = new ArrayList<>();
                for (int j = 0; j < metricNodes.getLength(); j++) {
                    Node metricNode = metricNodes.item(j);

                    if ( "Metric".equals(metricNode.getNodeName()) ) {
                        NamedNodeMap attributes = metricNode.getAttributes();
                        String name     = attributes.getNamedItem("name").getTextContent();
                        String process  = attributes.getNamedItem("process").getTextContent();
                        double value    = Double.parseDouble(metricNode.getTextContent());
                        metrics.add(new Metric(name, process, value));

                        // update percent complete
                        publish(percent(++numMetrics, totalMetrics));
                    }
                }

                // Add new DataValue to the List
                NamedNodeMap attributes = dataValueNode.getAttributes();
                int workload    = Integer.parseInt(attributes.getNamedItem("workload").getTextContent());
                Date start      = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(
                        attributes.getNamedItem("startTime").getTextContent());
                Date end        = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(
                                    attributes.getNamedItem("endTime").getTextContent());
                dataValues.add(new DataValue(workload, start, end, metrics));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        // Increment progress
       /* publish(percent(++currentLine, fileSize));

        // Create List for return
        this.list = new ArrayList<>();
        for (Map.Entry<String, PerformanceData> p : perfData.entrySet()) {
           list.add(p.getValue());
        }

        this.firePropertyChange("finished", null, true);

        // close ProgressWindow
        progress.dispose();
*/
        this.result = new PerformanceData(dataValues);
        return this.result;
        //return result;
    }

    protected void process(final List<Integer> chunks) {
        int last = 0;
        for (final int p : chunks) {
            this.firePropertyChange("progress", last, p);       // update progress
            last = p;                                           // update last value
        }
    }

    private int percent(int val, int maxVal) {
        double a = (double) val;
        double b = (double) maxVal;
        double result = (a/b) * 100.0;

        return (int) result;
    }
}
