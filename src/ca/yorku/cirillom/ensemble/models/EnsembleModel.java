package ca.yorku.cirillom.ensemble.models;

import java.util.*;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/12/14 1:40 PM.
 */
public class EnsembleModel extends Thread {

    private final List<PerformanceData> data;

    int window  = 10; // TODO: Make user configurable

    public EnsembleModel(List<PerformanceData> data) {
        this.data = data;
    }

    Map<String, ArrayDeque<Double>> avgMap = new HashMap<>();

    @Override
    public void run() {
        int offset = 0;
        int j = 0;
        int listSize = 0;

        while (!this.isInterrupted()) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (PerformanceData d : data) {
                for (String s : d.getMetrics()) {


                    List<DataValue> list = d.get(s);
                    listSize = list.size();
                    for(j = offset; j < listSize; j++) {
                        DataValue v = list.get(j);

                        insertValue(s, v.getValue());
                    }

                }
            }

            offset = j;

            // Print latest averages for each process
            //if (offset < listSize)
            for (PerformanceData d : data) {
                for (String s : d.getMetrics()) {
                    double avg = average(s);
                    System.out.println("["+offset+"]" + d.getProcess() + ": " + s + ": " + avg);
                }
            }
        }
    }

    private void insertValue(String metric, double average) {
        if (avgMap.containsKey(metric)) {
            ArrayDeque<Double> oldAvg = avgMap.get(metric);

            while(oldAvg.size() > window) {
                oldAvg.poll();
            }

            oldAvg.add(average);
        } else {
            ArrayDeque<Double> avg = new ArrayDeque<>();
            avg.add(average);
            avgMap.put(metric, avg);
        }
    }

    private double average(String metric) {
        double result = 0;
        for (Double aDouble : avgMap.get(metric)) {
            result += aDouble;
        }
        double size = avgMap.get(metric).size();
        return result / size;

    }
}
