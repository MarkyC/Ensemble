package ca.yorku.cirillom.ensemble.models;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/18/14 12:01 AM.
 * <p/>
 * Description
 * <p/>
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 */
public class ModelResult {

    private int workload;
    private String process;
    private String metric;
    private double actualValue;
    private double computedValue;
    private double errorPercent;

    public ModelResult(String process, String metric, int workload, double actualValue, double computedValue, double errorPercent) {
        this.process = process;
        this.metric = metric;
        this.workload = workload;
        this.actualValue = actualValue;
        this.computedValue = computedValue;
        this.errorPercent = errorPercent;
    }

    public String getProcess() {
        return process;
    }

    public String getMetric() {
        return metric;
    }

    public int getWorkload() {
        return workload;
    }

    public double getActualValue() {
        return actualValue;
    }

    public double getComputedValue() {
        return computedValue;
    }

    public double getErrorPercent() {
        return errorPercent;
    }

    public void setWorkload(int workload) {
        this.workload = workload;
    }


    @Override
    public String toString() {
        return "ModelResult{" +
                "process='" + process + '\'' +
                ", metric='" + metric + '\'' +
                ", actualValue=" + actualValue +
                ", computedValue=" + computedValue +
                ", errorPercent=" + errorPercent +
                '}';
    }
}
