package ca.yorku.cirillom.ensemble.models;

import java.util.ArrayDeque;
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
public class MovingAverageModel implements IEnsembleModel {

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

    private final ArrayDeque<DataValue> input;
    @Override
    public void addInput(DataValue input) {

        synchronized (this.input) {
            this.input.addLast(input);
            this.maintainWindow();
        }

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
    @Override
    public DataValue getLastInput() throws NullPointerException {
        synchronized (input) {
            if ( 0 == input.size() ) {
                throw new NullPointerException("No Last Input");
            }

            return input.peekLast();
        }
    }

    /* Constructor */

    public MovingAverageModel() {
        this(DEFAULT_WINDOW);
    }

    public MovingAverageModel(int window) {
        this.window = window;
        input = new ArrayDeque<>();
    }

    /* Functions */

    private void maintainWindow() {
        while (input.size() > window) {
            this.input.pop();
        }
    }

    @Override
    public double model() {
        double sum = 0;
        int size = 0;

        synchronized (input) {
            for (DataValue value : input) {
                sum += value.getValue();
                size++;
            }
        }

        setNextPrediction(sum / ((double) size));


        return getNextPrediction();
    }

    @Override
    public double getError() {
        // the last added value
        double lastInput        = getLastInput().getValue();
        double lastPrediction   = getLastPrediction();

        // prediction and input are both 0
        if ( doubleEquals(0, lastInput) && doubleEquals(0, lastPrediction) ) {
            return 0;
        }

        // prevent divide by 0
        if ( doubleEquals(0, lastInput) ) {
            return Double.POSITIVE_INFINITY;
        }

        // return percentage error, lower is more accurate
        return ( Math.abs(getLastPrediction() - lastInput) / lastInput );
    }

    private boolean doubleEquals(double a, double b) {
        return (Math.abs(a-b) < 0.0001);
    }
}
