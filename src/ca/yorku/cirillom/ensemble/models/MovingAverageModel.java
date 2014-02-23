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
        this.lastPrediction = this.nextPrediction;
        this.nextPrediction = result;
    }

    private double lastPrediction = 0;
    @Override public double getLastPrediction() {
        return lastPrediction;
    }


    private ArrayDeque<DataValue> input;
    @Override
    public void addInput(DataValue input) {
        this.input.addLast(input);

        this.maintainWindow();
    }
    @Override
    public void addInput(List<DataValue> input) {
        for (DataValue v : input) {
            this.addInput(v);
        }
    }
    public ArrayDeque<DataValue> getInput() {
        return input;
    }
    @Override
    public DataValue getLastInput() throws NullPointerException{
        if ( 0 == input.size() ) {
            throw new NullPointerException("No Last Input");
        }

        return input.peekLast();
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
        while (this.input.size() > window) {
            this.input.pop();
        }
    }

    @Override
    public double model() {
        double sum = 0;

        for (DataValue value : input.clone()) {
            sum += value.getValue();
        }

        setNextPrediction(sum / ((double) input.size()));

       /* System.out.println(input.size() + " " + window);

        if (input.size() == window) {

            String nextPrediction = "";
            for (DataValue value : input) {
                nextPrediction += value.getValue() + "\n";
            }

            nextPrediction += "lastValue: " + getLastInput() + "\n";
            nextPrediction += "lastPrediction: " + getLastPrediction() +"\n";
            nextPrediction += "accuracy: " + getError()+"\n";

            System.out.println(nextPrediction);

            System.exit(0);
        }*/

        return getNextPrediction();
    }

    @Override
    public double getError() {
        // the last added value
        double lastInput = getLastInput().getValue();

        // We haven't predicted a value yet, or prevent divide by 0
        if (0 == lastInput) return 0;//(Math.abs(lastInput - getLastPrediction()));

        // return percentage error, lower is more accurate
        return Math.abs(getLastPrediction() - lastInput) / lastInput;
    }
}
