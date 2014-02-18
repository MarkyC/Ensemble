package ca.yorku.cirillom.ensemble.models;

import java.util.ArrayDeque;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/17/14 9:13 PM.
 * <p/>
 * Description
 * <p/>
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 */
public class MovingAverageModel implements IEnsembleModel {

    private final int window;

    private double result = 0;
    private double lastResult = 0;

    private static final int DEFAULT_WINDOW = 10;

    ArrayDeque<DataValue> data;

    public MovingAverageModel() {
        this(DEFAULT_WINDOW);
    }

    public MovingAverageModel(int window) {
        this.window = window;
        data = new ArrayDeque<>();
    }

    @Override
    public void add(DataValue value) {

        System.out.println(this + " adding:" + value.getValue());
        while (data.size() >= window) {
            data.pop();
        }

        data.addLast(value);
    }

    @Override
    public double model() {
        double sum = 0;

        for (DataValue value : data) {
            sum += value.getValue();
        }

        setResult(sum / ((double) data.size()));

        System.out.println(data.size() + " " + window);

        if (data.size() == window) {

            String output = "";
            for (DataValue value : data) {
                output += value.getValue() + "\n";
            }

            output += "lastValue: " + getLastValue() + "\n";
            output += "lastResult: " + getLastResult() +"\n";
            output += "accuracy: " + getAccuracy()+"\n";

            System.out.println(output);

            System.exit(0);
        }

        return getResult();
    }

    private void setResult(double result) {
        this.lastResult = this.result;
        this.result = result;
    }

    @Override
    public double getResult() {
        return result;
    }

    @Override
    public double getLastResult() {
        return lastResult;
    }

    @Override
    public double getLastValue() {
        if ( 0 == data.size() ) return 0;

        return data.pollLast().getValue();
    }

    @Override
    public double getAccuracy() {

        // Actual Value is the last added value
        double actualValue = getLastValue();

        if (0 == actualValue) return (Math.abs(actualValue - this.lastResult));

        return (Math.abs(actualValue - this.lastResult) / actualValue);
    }
}
