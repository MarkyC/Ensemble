package ca.yorku.cirillom.ensemble.modellers;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.ModelResult;
import opera.KalmanFilter.EstimationResults;
import opera.KalmanFilter.KalmanConfiguration;
import opera.KalmanFilter.KalmanEstimator;

import java.util.Arrays;
import java.util.List;

/**
 * OperaModel.java
 *
 * @author Marco
 * @since 2014-04-04
 */
public class OperaModel implements IEnsembleModel {

    // TODO: pass these files in
    public static final String PXL_FILE     = "C:\\Users\\Marco\\Desktop\\performance logs\\opera sample.pxl";
    public static final String CONFIG_FILE  = "C:\\Users\\Marco\\Desktop\\performance logs\\opera kalman.config";

    String[] scenarios = {"Insert", "Update", "Select0", "Select1", "Select2", "Select3" };
    double[] measuredMetrics = {42.0055, 41.3907, 70.0613, 270.7935, 780.1026, 1975.6667};
    double[] workloads = {5.5, 7.7, 8.8, 11.1, 22.2, 55.5, 1.1};

    private final opera.OperaModel model            = new opera.OperaModel();
    private final KalmanConfiguration kalmanConfig  = new KalmanConfiguration();
    private final KalmanEstimator estimator;

    public OperaModel() {
        model.setModel(PXL_FILE);
        kalmanConfig.withConfigFile(CONFIG_FILE)
                .withModel(model)
                .withSetting(KalmanConfiguration.ITERATIONS_MAX, "20");
        estimator = new KalmanEstimator(kalmanConfig);

    }
    @Override
    public void addInput(DataValue input) {
        for (int i = 0; i < scenarios.length; i++) {
            String scenario = scenarios[i];
            double workload = workloads[i];
            double thinkTime = 3000;
            double respTime = measuredMetrics[i];
            model.SetPopulation(scenario, workload / 1000 * (thinkTime + respTime));
        }

        model.solve();

        EstimationResults results = estimator.EstimateModelParameters(Arrays.copyOf(measuredMetrics, 1));
        System.out.println(results.toString());
    }

    @Override
    public void addInput(List<DataValue> input) {

    }

    @Override
    public DataValue getLastInput() throws NullPointerException {
        return null;
    }

    @Override
    public List<ModelResult> model() {
        return null;
    }

    @Override
    public List<ModelResult> getResults() {
        return null;
    }

    @Override
    public List<ModelResult> predict(int workload) {
        return null;
    }
}
