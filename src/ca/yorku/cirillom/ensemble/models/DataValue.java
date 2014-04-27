package ca.yorku.cirillom.ensemble.models;

import java.util.Date;
import java.util.List;

/**
 * @author Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 11:12 PM.
 * A DataValue is a POJO that stores:
 * <ul>
 *     <li>Metrics</li>
 *     <li>Workloads</li>
 *     <li>The the time the data collection begun (start time)</li>
 *     <li>The the time the data collection finished (end time)</li>
 * </ul>
 */
public class DataValue {

    private List<Workload> workloads;
    private Date startTime;
    private Date endTime;
    private List<Metric> metrics;

    /**
     * A DataValue is
     * @param startTime The the time the data collection begun
     * @param endTime The the time the data collection finished
     * @param metrics A List of Metrics
     * @param workloads A List of Workloads
     */
    public DataValue(Date startTime, Date endTime, List<Metric> metrics, List<Workload> workloads) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.metrics = metrics;
        this.workloads = workloads;
    }

    /**
     * Returns the sum of all requests for each workload
     * @return the total workload (sum of all requests)
     */
    public int getTotalWorkload() {
        return getWorkloads().size();
    }

    /**
     *
     * @return A list of all the pages requested between this DataValue's start and end times
     */
    public List<Workload> getWorkloads() {
        return workloads;
    }

    /**
     * @return The the time the data collection begun
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @return The the time the data collection finished
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @return The List of Metrics this class contains
     */
    public List<Metric> getMetrics() {
        return metrics;
    }

    public Metric getMetricByNameAndProcess(String name, String process) {
        Metric m = null;

        for (Metric metric : metrics) {
            if (name.equals(metric.getName()) && process.equals(metric.getProcess())) {
                m = metric;
            }
        }

        return m;
    }

    /**
     * Adds a Metric m to the list of Metrics
     * @param m The Metric to add
     * @return true if the add was successful, false otherwise
     */
    public boolean addMetric(Metric m) {
        return metrics.add(m);
    }

    public double getRequestsPerSecond(String workloadName) {
        double requests = 0;
        for (Workload w : getWorkloads()) {
            if (workloadName.equalsIgnoreCase(w.getResource())) {
                requests++;
            }
        }

        double time = endTime.getTime() - startTime.getTime(); // in milliseconds
        return requests / (time / 1000); // number of requests over the time interval (converted to seconds)
    }

    @Override
    public String toString() {
        return "DataValue{" +
                "workloads=" + workloads +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", metrics=" + metrics +
                '}';
    }
}
