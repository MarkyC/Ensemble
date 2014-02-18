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

    private String process;
    private String metric;
    private double actualValue;
    private double computedValue;
    private double accuracy;

    public ModelResult(String process, String metric, double actualValue, double computedValue, double accuracy) {
        this.process = process;
        this.metric = metric;
        this.actualValue = actualValue;
        this.computedValue = computedValue;
        this.accuracy = accuracy;
    }

    public String getProcess() {
        return process;
    }

    public String getMetric() {
        return metric;
    }

    public double getActualValue() {
        return actualValue;
    }

    public double getComputedValue() {
        return computedValue;
    }

    public double getAccuracy() {
        return accuracy;
    }
}
