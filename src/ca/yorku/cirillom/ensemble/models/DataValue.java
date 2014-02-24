package ca.yorku.cirillom.ensemble.models;

import java.util.Date;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 11:12 PM.
 * A DataValue is a POJO that stores a performance monitoring value and the time it was collected
 */
public class DataValue implements Cloneable{

    private double value;
    private Date time;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public DataValue(double value, Date time) {
        this.value = value;
        this.time = time;
    }

    @Override
    public String toString() {
        return "DataValue{" +
                "value=" + value +
                ", time=" + time +
                '}';
    }

    @Override
    public DataValue clone() {
        return new DataValue(getValue(), getTime());
    }
}
