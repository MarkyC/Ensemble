package ca.yorku.cirillom.ensemble.modellers;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.Metric;
import ca.yorku.cirillom.ensemble.models.ModelResult;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.*;

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
                regressions.get(i).addData(input.getTotalWorkload(), metric.getValue());
            } catch (IndexOutOfBoundsException ex) {

                // Create a new SimpleRegression for this index in the list
                SimpleRegression r = new SimpleRegression();
                r.addData(input.getTotalWorkload(), metric.getValue());
                regressions.add(i, r);
            }
        }

        this.lastInput = input;

    }

    //@Override
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

    //@Override
    public List<ModelResult> model() {

        // Create ModelResult Array based on computed results
        List<ModelResult> results = new ArrayList<>();
        DataValue latestDataValue = inputs.get(inputs.size() - 1);
        List<Metric> metrics = latestDataValue.getMetrics();
        for (int i = 0; i < metrics.size(); i++) {
            Metric metric = metrics.get(i);

            double computedResult = regressions.get(i).predict(latestDataValue.getTotalWorkload());

            // Create results
            results.add(new ModelResult(
                    metric.getProcess(),
                    metric.getName(),
                    latestDataValue.getTotalWorkload(),
                    metric.getValue(),
                    computedResult,
                    regressions.get(i).getMeanSquareError()
            ));
        }

        setResults(results);
        return results;
    }


    @Override
    public List<ModelResult> predict(DataValue value) {
        int workload = value.getTotalWorkload();

        List<ModelResult> results = new ArrayList<>();
        DataValue latestDataValue = inputs.get(inputs.size() - 1);
        List<Metric> metrics = latestDataValue.getMetrics();
        for (int i = 0; i < metrics.size(); i++) {
            Metric metric = metrics.get(i);

            double computedResult = regressions.get(i).predict(workload);

            double actualValue = Double.NaN;
            for (DataValue v : inputs) {
                if (v.getTotalWorkload() == workload) {
                    actualValue = v.getMetrics().get(i).getValue();
                }
            }

            // Create results
            results.add(new ModelResult(
                    metric.getProcess(),
                    metric.getName(),
                    workload,
                    actualValue,
                    computedResult,
                    regressions.get(i).getMeanSquareError()
            ));
        }

        return results;
    }

    private List<Map<Integer, Double>> getActualValues(List<DataValue> inputs) {

        List<Map<Integer, Double>> results = new ArrayList<>(inputs.size());

        for (DataValue input : inputs) {
            List<Metric> metrics = input.getMetrics();
            for (int i = 0; i < metrics.size(); i++) {

                Map<Integer, Double> actuals = null;
                try {
                    actuals = results.get(i);
                } catch (IndexOutOfBoundsException e) {
                    actuals = new LinkedHashMap<>();
                }

                actuals.put(input.getTotalWorkload(), metrics.get(i).getValue());
                results.add(i, actuals);

            }
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
