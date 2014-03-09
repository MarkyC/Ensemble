package ca.yorku.cirillom.ensemble.models;

import java.util.Date;
import java.util.List;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 11:12 PM.
 * A DataValue is a POJO that stores a performance monitoring value and the time it was collected
 */
public class DataValue {

    private int workload;
    private Date startTime;
    private Date endTime;
    private List<Metric> metrics;

    public DataValue(int workload, Date startTime, Date endTime, List<Metric> metrics) {
        this.workload = workload;
        this.startTime = startTime;
        this.endTime = endTime;
        this.metrics = metrics;
    }

    public int getWorkload() {
        return workload;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public boolean addMetric(Metric m) {
        return metrics.add(m);
    }

    @Override
    public String toString() {
        return "DataValue{" +
                "workload=" + workload +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", metrics=" + metrics +
                '}';
    }
}
