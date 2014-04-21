package ca.yorku.cirillom.ensemble.modellers;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.Metric;
import ca.yorku.cirillom.ensemble.models.ModelResult;
import ca.yorku.cirillom.ensemble.models.Workload;
import ca.yorku.cirillom.ensemble.preferences.Preferences;
import opera.KalmanFilter.EstimationResults;
import opera.KalmanFilter.KalmanConfiguration;
import opera.KalmanFilter.KalmanEstimator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OperaModel.java
 *
 * @author Marco
 * @since 2014-04-04
 */
public class OperaModel implements IEnsembleModel {

    public static final String PROCESSOR_TIME = "% Processor Time";

    private DataValue lastInput = null;
    private List<ModelResult> lastOutput = new ArrayList<>();

    private final opera.OperaModel model = new opera.OperaModel();
    private final KalmanEstimator estimator;

    private static final String PXL_FILE    = Preferences.getInstance().get( "OperaModel-pxl" );
    private static final String CONFIG_FILE = Preferences.getInstance().get( "OperaModel-kalman" );

    private static final List<String> SCENARIOS = Preferences.getInstance().getAsList( "OperaModel-scenarios" );
    private static final List<String> PROCESSES = Preferences.getInstance().getAsList( "OperaModel-processes" );

    /**
     * Matches 1-1 with PROCESSES. That is, each node runs exactly 1 PROCESS
     * ie: PROCESSES[0]=mysqld => NODES[0]=WebHost means mysqld runs on WebHost
     */
    private static final List<String> NODES     = Preferences.getInstance().getAsList( "OperaModel-nodes" );


    public OperaModel() {

        // Check if we have the required settings set up to run OPERA
        if ( !validSettings() ) {
            throw new IllegalArgumentException(
                    "PXL, Kalman, and Scenario's must exist in settings.\n" + getSettings() );
        }

        // Set up OPERA with the supplied pxl and config files
        model.setModel(PXL_FILE);
        KalmanConfiguration kalmanConfig = new KalmanConfiguration();
        kalmanConfig.withConfigFile(CONFIG_FILE)
                .withModel(model)
                .withSetting(KalmanConfiguration.ITERATIONS_MAX, "20");
        estimator = new KalmanEstimator(kalmanConfig);

    }

    private boolean validSettings() {
        boolean isValid = (null != PXL_FILE) && (null != CONFIG_FILE) &&
                            (SCENARIOS.size() > 0) && (PROCESSES.size() > 0) && (NODES.size() > 0);

        if ( isValid ) {
            File pxl    = new File(PXL_FILE);
            File kalman = new File(CONFIG_FILE);

            isValid = pxl.exists() && kalman.exists();
        }

        return isValid;
    }

    private String getSettings() {
        return "PXL: " + PXL_FILE +
                "\nKALMAN: " + CONFIG_FILE +
                "\nSCENARIOS: " + SCENARIOS.toString() +
                "\nPROCESSES: " + PROCESSES.toString() +
                "\nNODES: " + NODES.toString();
    }

    @Override
    public void addInput(DataValue input) {

        this.lastInput = input;

        setPopulations(input);
        calibrate(input);
        System.out.println("solved");
    }

    private void setPopulations(DataValue value) {
        List<Workload> workloadList = value.getWorkloads();
        for (Metric m : value.getMetrics()) {

            /* OPERA only allows CPU to be measured */
            if (PROCESSOR_TIME.equals(m.getName())) {

                /* Our Workload's map to OPERA's Scenarios */
                for (Workload w : workloadList) {
                    String scenario = w.getResource();
                    double workload = value.getRequestsPerSecond(scenario);
                    double responseTime = w.getResponseTime();
                    double thinkTime = 3000; // TODO: make this a (user-configurable?) variable

                    // Add to OPERA
                    model.SetPopulation(scenario, workload / 1000 * (thinkTime + responseTime));
                }
            }
        }
    }

    @Override
    public DataValue getLastInput(){
        return this.lastInput;
    }

    @Override
    public List<ModelResult> getResults() {
        return this.lastOutput;
    }

    private void setResults(List<ModelResult> results) {
        this.lastOutput = results;
    }

    private EstimationResults calibrate(DataValue value) {
        List<Double> measuredMetrics = getMeasuredMetrics(value);
        return estimator.EstimateModelParameters(listToDoubleArray(measuredMetrics));
    }

    private static double[] listToDoubleArray(List<Double> list) {
        double[] arr = new double[list.size()];

        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);

        return arr;
    }

    @Override
    public List<ModelResult> predict(DataValue value) {
        System.out.println("predicting");

        setPopulations(value);
        model.solve();

        List<ModelResult> results = new ArrayList<>();

        for (int i = 0; i < NODES.size(); i++) {
            double cpu = model.GetUtilizationNode(NODES.get(i), "CPU");
            results.add(new ModelResult(PROCESSES.get(i), PROCESSOR_TIME, getLastInput().getTotalWorkload(), 0, cpu, 100));
        }

        this.setResults(results);

        return results;
    }

    public List<Double> getMeasuredMetrics(DataValue value) {
        List<Double> result = new ArrayList<>();

        Map<String, Double> metricMap = getMetricValues(value.getMetrics());
        Map<String, Double> responseTimeMap = getResponseTimes(value.getWorkloads());
        Map<String, Double> requestsMap = getRequestsPerSecond(value, value.getWorkloads());

        for (String p : PROCESSES) {
            if (metricMap.containsKey(p)) {
                result.add(metricMap.get(p));
            } else {
                //throw new IllegalArgumentException("No Metric found for process " + p);
                result.add(50d);
            }
        }

        for (String s : SCENARIOS) {
            if (responseTimeMap.containsKey(s)) {
                result.add(responseTimeMap.get(s));
            } else {
                //throw new IllegalArgumentException("No response time found for scenario " + s);
                result.add(500d);
            }
        }

        for (String s : SCENARIOS) {
            if (requestsMap.containsKey(s)) {
                result.add(requestsMap.get(s));
            } else {
                //throw new IllegalArgumentException("No requests/second found for scenario " + s);
                result.add(1d);
            }
        }

        return result;
    }

    private Map<String, Double> getMetricValues(List<Metric> metrics) {
        Map<String, Double> result = new HashMap<>();

        for (String p : PROCESSES) {
            for (Metric m : metrics) {
                if (PROCESSOR_TIME.equals(m.getName())) {
                    if (p.equals(m.getProcess())) {
                        result.put(p, m.getValue());
                    }
                }
            }
        }

        if (0 == result.size()) {
            throw new IllegalStateException("Unable to get Measured Metrics from processes: " + PROCESSES.toString());
        }
        return result;
    }

    private Map<String, Double> getResponseTimes(List<Workload> workloads) {
        Map<String, Double> result = new HashMap<>();

        for (String s : SCENARIOS) {
            for (Workload w : workloads) {

                if (s.equals(w.getResource())) {
                    result.put(s, w.getResponseTime());
                }
            }
        }

        if (0 == result.size()) {
            throw new IllegalStateException("Unable to get Response Times from scenarios: " + SCENARIOS.toString());
        }
        return result;
    }

    private Map<String, Double> getRequestsPerSecond(DataValue value, List<Workload> workloads) {
        Map<String, Double> result = new HashMap<>();

        for (String s : SCENARIOS) {
            for (Workload w : workloads) {
                if (s.equals(w.getResource())) {
                    result.put(s, value.getRequestsPerSecond(w.getResource()));
                }
            }
        }

        if (0 == result.size()) {
            throw new IllegalStateException("Unable to get Requests from scenarios: " + SCENARIOS.toString());
        }
        return result;
    }
}
