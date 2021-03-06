package ca.yorku.cirillom.ensemble.modellers;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.Metric;
import ca.yorku.cirillom.ensemble.models.ModelResult;
import ca.yorku.cirillom.ensemble.preferences.Preferences;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
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
    public static final String WINDOW_PREFERENCE = "MovingAverageModel-window";

    /* Fields and Accessors */

    private int window;
    public int getWindow() {
        return window;
    }
    public void setWindow(int window) {

        Preferences.getInstance().put(WINDOW_PREFERENCE, ""+window);
        this.window = window;
    }

    /**
     * Model Results
     */
    private List<ModelResult> results = new ArrayList<>();
    public List<ModelResult> getResults() {
        return results;
    }
    public void setResults(List<ModelResult> results) {
        this.results = results;
    }

    private final ArrayDeque<DataValue> input;
    @Override
    public void addInput(DataValue input) {

        synchronized (this.input) {
            this.input.addLast(input);
            this.maintainWindow();
        }

    }

    @Override
    public DataValue getLastInput() {
        synchronized (input) {
            return input.peekLast();
        }
    }

    /* Constructor */

    public MovingAverageModel() {

        // Grab the user's stored preference for the window size
        String windowPref = Preferences.getInstance().get(WINDOW_PREFERENCE);

        // Use the setter so the user's preference is saved
        try {
            this.setWindow(Integer.parseInt(windowPref));
        } catch (Exception e) {
            // Due to inconsistency in Java's API, parseInt throws only a NumberFormatEx
            // parseDouble throws NullPointerEx if windowPref is null
            // We catch Exception instead of NumberFormatEx here to be safe (perhaps Java fixes this one day!)
            this.setWindow(DEFAULT_WINDOW);
        }

        this.input = new ArrayDeque<>();
    }

    /* Functions */

    private void maintainWindow() {
        while (input.size() > window) {
            this.input.pop();
        }
    }

    //@Override
    public List<ModelResult> model() {

        // Compute Results
        List<Double> computedResults = createFilledListOfDouble(input.size());
        synchronized (input) {
            for (DataValue value : input) {
                List<Metric> metrics = value.getMetrics();
                for (int i = 0; i < metrics.size(); i++) {
                    Metric metric = metrics.get(i);
                    computedResults.add(i, computedResults.get(i) + metric.getValue());
                }
            }
        }


        // Create ModelResult Array based on computed results
        List<ModelResult> results = new ArrayList<>();
        List<Metric> metrics = input.getLast().getMetrics();
        int size = input.size();
        for (int i = 0; i < metrics.size(); i++) {
            Metric metric = metrics.get(i);

            double computedResult = computedResults.get(i) / size;

            /*if (metric.getName().equals("% Processor Time")) {
                System.out.print(computedResults);
                System.out.println(metric.getValue() + " " + computedResult);
            }*/

            // Create results
            results.add(new ModelResult(
                    metric.getProcess(),
                    metric.getName(),
                    input.getLast().getTotalWorkload(),
                    metric.getValue(),
                    computedResult,
                    getError(metric.getValue(), computedResult)
            ));
        }

        setResults(results);
        return results;
    }

    public List<ModelResult> predict(DataValue value) {
        int workload = value.getTotalWorkload();

        List<Double> predictions = new ArrayList<>();

        for (Iterator<DataValue> it = input.iterator(); it.hasNext(); ) {
            List<Metric> metrics = it.next().getMetrics();
            for (int i = 0; i < metrics.size(); i++) {
                if (i >= predictions.size()) {
                    predictions.add(i, metrics.get(i).getValue());
                } else {
                    predictions.set(i, predictions.get(i) + metrics.get(i).getValue());
                }
            }
        }

        for (int i = 0; i < predictions.size(); i++) {
            predictions.set(i, predictions.get(i)/input.size());
        }

        List<ModelResult> results = new ArrayList<>();

        List<Metric> metrics = input.getLast().getMetrics();
        for (int i = 0; i < metrics.size(); i++) {
            Metric metric = metrics.get(i);
            ModelResult mr = new ModelResult(
                    metric.getProcess(),
                    metric.getName(),
                    input.getLast().getTotalWorkload(),
                    metric.getValue(),
                    predictions.get(i),
                    getError(metric.getValue(), predictions.get(i))
            );
            results.add(mr);
        }

        return results;
    }

    /*public List<ModelResult> predict(int workload) {
        List<ModelResult> results = new ArrayList<>(getResults().size());
        for (ModelResult mr : model()) {
            mr.setWorkload(workload);
            results.add(mr);
        }

        return results;
    }*/

    public double getError(double predicted, double actual) {
        return Math.abs(predicted - actual)/actual;
    }

    /* Helper Methods */

    private List<Double> createFilledListOfDouble(int size) {
        List<Double> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(0d);
        }
        return result;
    }

    private boolean doubleEquals(double a, double b) {
        return (Math.abs(a-b) < 0.0001);
    }
}
