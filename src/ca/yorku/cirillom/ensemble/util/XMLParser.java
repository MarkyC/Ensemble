package ca.yorku.cirillom.ensemble.util;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.Metric;
import ca.yorku.cirillom.ensemble.models.PerformanceData;
import ca.yorku.cirillom.ensemble.models.Workload;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
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

    /* PerformanceData.xml Node/Attribute constants */
    public static final String METRIC       = "Metric";
    public static final String WORKLOAD     = "Workload";
    public static final String DATA_VALUE   = "DataValue";
    public static final String NAME         = "name";
    public static final String PROCESS      = "process";
    public static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";
    public static final String START_TIME   = "startTime";
    public static final String END_TIME     = "endTime";

    /* Used to indicate progress so MainWindow can update the ProgressWindow */
    public static final String PROGRESS = "progress";

    private final File file;
    //private List<PropertyChangeListener> listeners = new ArrayList<>();

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

            int progress      = 0;  // Number of Metrics and Workloads Processed
            int totalProgress = xml.getElementsByTagName(METRIC).getLength()
                                + xml.getElementsByTagName(WORKLOAD).getLength(); // Total Number of Metrics

            NodeList dataValueNodes = xml.getElementsByTagName(DATA_VALUE);

            for (int i = 0; i < dataValueNodes.getLength();i++) {

                // get item for this iteration
                Node dataValueNode = dataValueNodes.item(i);

                // Child Nodes should be Metrics and Workloads
                NodeList children = dataValueNode.getChildNodes();

                // Fill Metric List for this DataValue
                List<Metric> metrics    = new ArrayList<>();
                List<Workload> workloads= new ArrayList<>();

                for (int j = 0; j < children.getLength(); j++) {

                    Node child = children.item(j);                      // The Child Node
                    NamedNodeMap attributes = child.getAttributes();    // It's attributes <Node attribute="textContent"


                    if ( METRIC.equals(child.getNodeName()) ) {

                        String name     = attributes.getNamedItem(NAME).getTextContent();
                        String process  = attributes.getNamedItem(PROCESS).getTextContent();
                        double value    = Double.parseDouble(child.getTextContent());

                        metrics.add(new Metric(name, process, value));

                    } else if (WORKLOAD.equals(child.getNodeName())) {

                        String  name         = attributes.getNamedItem(NAME).getTextContent();
                        int     responseTime = Integer.parseInt(child.getTextContent());
                        //double  responseTime = Double.parseDouble(attributes.getNamedItem("responseTime").getTextContent());

                        workloads.add(new Workload(name, responseTime));
                    }

                    // update percent complete
                    publish(percent(++progress, totalProgress));
                }

                // Add new DataValue to the List
                NamedNodeMap attributes = dataValueNode.getAttributes();
                /*int workload    = Integer.parseInt(attributes.getNamedItem("workload").getTextContent());*/
                Date start      = new SimpleDateFormat(DATE_PATTERN).parse(
                        attributes.getNamedItem(START_TIME).getTextContent());
                Date end        = new SimpleDateFormat(DATE_PATTERN).parse(
                                    attributes.getNamedItem(END_TIME).getTextContent());
                dataValues.add(new DataValue(start, end, metrics, workloads));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        this.result = new PerformanceData(dataValues);
        return this.result;
    }

    protected void process(final List<Integer> chunks) {
        int last = 0;
        for (final int p : chunks) {
            this.firePropertyChange(PROGRESS, last, p);       // update progress
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
