package ca.yorku.cirillom.ensemble.models;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 3/7/14 5:32 PM.
 * <p/>
 * Description
 * <p/>
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 */
public class Metric {

    private String name;
    private String process;
    private double value;

    public Metric(String name, String process, double value) {
        this.name = name;
        this.process = process;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getProcess() {
        return process;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metric metric = (Metric) o;

        if (Double.compare(metric.value, value) != 0) return false;
        if (!name.equals(metric.name)) return false;
        if (!process.equals(metric.process)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        result = 31 * result + process.hashCode();
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "name='" + name + '\'' +
                ", process='" + process + '\'' +
                ", value=" + value +
                '}';
    }
}
