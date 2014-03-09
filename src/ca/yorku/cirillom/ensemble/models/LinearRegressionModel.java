package ca.yorku.cirillom.ensemble.models;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
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

    /* Fields and Accessors */

    private List<ModelResult> results;
    @Override
    public List<ModelResult> getResults() {
        return this.results;
    }
    public void setResults(List<ModelResult> results) {
        this.results = results;
    }


    //private final SimpleRegression input = new SimpleRegression();
    final private List<DataValue> input = new ArrayList<>();
    @Override
    public void addInput(DataValue input) {

        synchronized (this.input) {
            this.input.add(input);
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
    }

    /* Functions */

    @Override
    public List<ModelResult> model() {

        // Compute Results
        List<SimpleRegression> computedResults = createFilledListOfRegression(input.get(0).getMetrics().size());
        synchronized (input) {
            for (DataValue value : input) {
                List<Metric> metrics = value.getMetrics();
                for (int i = 0; i < metrics.size(); i++) {
                    Metric metric = metrics.get(i);
                    computedResults.get(i).addData(value.getWorkload(), metric.getValue());
                }
            }
        }

        // Create ModelResult Array based on computed results
        List<ModelResult> results = new ArrayList<>();
        DataValue latestDataValue = input.get(input.size() - 1);
        List<Metric> metrics = latestDataValue.getMetrics();
        for (int i = 0; i < metrics.size(); i++) {
            Metric metric = metrics.get(i);

            double computedResult = computedResults.get(i).predict(metric.getValue());

            // Create results
            results.add(new ModelResult(
                    metric.getProcess(),
                    metric.getName(),
                    metric.getValue(),
                    computedResult,
                    computedResults.get(i).getMeanSquareError()
            ));
        }

        setResults(results);
        return results;
    }


   /* @Override
    public double getError() {

        return input.getMeanSquareError();

    }*/

    private List<SimpleRegression> createFilledListOfRegression(int size) {
        List<SimpleRegression> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new SimpleRegression());
        }
        return result;
    }
}
