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
    List<SimpleRegression> regressions = new ArrayList<>();
    List<DataValue> inputs = new ArrayList<>();
    @Override
    public void addInput(DataValue input) {

        this.inputs.add(input);

        List<Metric> metrics = input.getMetrics();
        for (int i = 0; i < metrics.size(); i++) {
            Metric metric = metrics.get(i);
            try {

                // Add data to existing SimpleRegression
                regressions.get(i).addData(input.getWorkload(), metric.getValue());
            } catch (IndexOutOfBoundsException ex) {

                // Create a new SimpleRegression for this index in the list
                SimpleRegression r = new SimpleRegression();
                r.addData(input.getWorkload(), metric.getValue());
                regressions.add(i, r);
            }
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

        // Create ModelResult Array based on computed results
        List<ModelResult> results = new ArrayList<>();
        DataValue latestDataValue = inputs.get(inputs.size() - 1);
        List<Metric> metrics = latestDataValue.getMetrics();
        for (int i = 0; i < metrics.size(); i++) {
            Metric metric = metrics.get(i);

            double computedResult = regressions.get(i).predict(latestDataValue.getWorkload());

            // Create results
            results.add(new ModelResult(
                    metric.getProcess(),
                    metric.getName(),
                    latestDataValue.getWorkload(),
                    metric.getValue(),
                    computedResult,
                    regressions.get(i).getMeanSquareError()
            ));
        }

        setResults(results);
        return results;
    }


    @Override
    public List<ModelResult> predict(int workload) {
        List<ModelResult> results = new ArrayList<>();
        DataValue latestDataValue = inputs.get(inputs.size() - 1);
        List<Metric> metrics = latestDataValue.getMetrics();
        for (int i = 0; i < metrics.size(); i++) {
            Metric metric = metrics.get(i);

            double computedResult = regressions.get(i).predict(workload);

            // Create results
            results.add(new ModelResult(
                    metric.getProcess(),
                    metric.getName(),
                    workload,
                    /*metric.getValue()*/Double.NaN,
                    computedResult,
                    regressions.get(i).getMeanSquareError()
            ));
        }

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
