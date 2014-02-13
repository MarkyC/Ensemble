package ca.yorku.cirillom.ensemble.models;

import java.util.*;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 11:10 PM.
 * Holds a list of DataValues for each metric the PerformanceData is watching,
 * for a specific process. A metric could be "CPU usage" for example.
 * It's DataValues would hold the CPU usage for a given time.
 */
public class PerformanceData {

    private Map<String, List<DataValue>> metrics;

    private String process;

    public PerformanceData(String process) {
        this(process, new HashMap<String, List<DataValue>>());
    }

    public PerformanceData(String process, Map<String, List<DataValue>> metrics) {

        this.process = process;
        this.metrics = metrics;
    }


    public String getProcess() {
        return process;
    }

    public Set<String> getMetrics() {
        return metrics.keySet();
    }

    public boolean has(String metric) {
        return metrics.containsKey(metric);
    }


    public List<DataValue> add(String metric, DataValue value) {

        // If metric exists, append new value, else create a new list
        List<DataValue> list = has(metric) ? get(metric) : new ArrayList<DataValue>();

        // Add metric
        list.add(value);
        return metrics.put(metric, list);
    }

    public List<DataValue> get(String metric) {
        return metrics.get(metric);
    }

    private static String metricsToString(Map<String, List<DataValue>> metrics) {
        String result = "{ ";

        for (String metric : metrics.keySet()) {
            result += "[ " + metric + " ";
            for (DataValue val : metrics.get(metric)) {
                result += val.toString() + " ";
            }
            result += " ] ";
        }

        return result + "} ";
    }

    @Override
    public String toString() {
        return "PerformanceData{" +
                "process='" + process + '\'' +
                "metrics=" + metricsToString(metrics) +
                '}';
    }
}
