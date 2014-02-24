package ca.yorku.cirillom.ensemble.models;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.List;

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
public class LinearRegressionModel implements IEnsembleModel {

    /* Constants */
    public static final int DEFAULT_WINDOW = 10;

    /* Fields and Accessors */

    private int window;
    public int getWindow() {
        return window;
    }
    public void setWindow(int window) {
        this.window = window;
    }

    private double nextPrediction = 0;
    @Override public double getNextPrediction() {
        return nextPrediction;
    }
    private void setNextPrediction(double result) {
        setLastPrediction(this.nextPrediction);
        this.nextPrediction = result;
    }

    private double lastPrediction = 0;
    @Override public double getLastPrediction() { return lastPrediction; }
    public void setLastPrediction(double lastPrediction) { this.lastPrediction = lastPrediction; }

    private final SimpleRegression input = new SimpleRegression();
    @Override
    public void addInput(DataValue input) {

        synchronized (this.input) {
            this.input.addData(input.getTime().getTime(), input.getValue());
        }

        this.lastInput = input;

    }
    @Override
    public void addInput(List<DataValue> input) {
        for (DataValue v : input) {
            this.addInput(v);
        }
    }
    /*public ArrayDeque<DataValue> getInput() {
        return input;
    }*/

    private volatile DataValue lastInput = null;
    @Override
    public DataValue getLastInput() throws NullPointerException {
        return this.lastInput;
    }

    /* Constructor */

    public LinearRegressionModel() {
        this(DEFAULT_WINDOW);
    }

    public LinearRegressionModel(int window) {
        this.window = window;
    }

    /* Functions */

    @Override
    public double model() {

        synchronized (input) {
            setNextPrediction(input.predict(this.getLastInput().getTime().getTime()));
        }

        return getNextPrediction();
    }

    @Override
    public double getError() {

        return input.getMeanSquareError();

    }

    private boolean doubleEquals(double a, double b) {
        return (Math.abs(a-b) < 0.0001);
    }
}
