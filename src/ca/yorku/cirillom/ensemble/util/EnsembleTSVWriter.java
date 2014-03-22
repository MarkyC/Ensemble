package ca.yorku.cirillom.ensemble.util;

import ca.yorku.cirillom.ensemble.models.ModelResult;

import java.util.*;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 3/21/2014 12:31 PM.
 * <p/>
 * Description
 * <p/>
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 */
public class EnsembleTSVWriter {

    private List<EnsembleTSVRow> rows = new ArrayList<>();

    List<ModelResult> results = new ArrayList<>();

    public void addModelResults(List<ModelResult> results) {
        for (ModelResult result : results) {
            //rows.add(new EnsembleTSVRow(result));
            results.add(result);
        }
    }

    public void addModelResult(ModelResult result) {
        results.add(result);
    }

    public void write() {

        // Iterate through each Process/Metric
        Map<String, List<ModelResult>> theRows = splitByProcesMetric(results);
        for (Iterator<Map.Entry<String, List<ModelResult>>> it = theRows.entrySet().iterator(); it.hasNext();) {
            Map<Integer, List<ModelResult>> resultsByNumber = splitByResultNumber(it.next().getValue());

            // Iterate over each result, in order
            for (Iterator<Map.Entry<Integer, List<ModelResult>>> it2 = resultsByNumber.entrySet().iterator();
                 it2.hasNext();) {

                Map.Entry<Integer, List<ModelResult>> next  = it2.next();
                Integer resultNumber                        = next.getKey();
                List<ModelResult> mr                        = next.getValue();

                double totalAccuracy = 0;
                for (ModelResult r : mr) {
                    totalAccuracy += 1 - accuracy(r.getActualValue(), r.getComputedValue());
                    //System.out.println(1 - accuracy(r.getActualValue(), r.getComputedValue())+ " " +totalAccuracy);
                }

                for (ModelResult r : mr) {
                    double bagging = 0.5 * r.getComputedValue();
                    double stacking= (( 1 - accuracy(r.getActualValue(), r.getComputedValue()) ) / totalAccuracy)
                                         * r.getComputedValue();

                    //System.out.println(new EnsembleTSVRow(resultNumber, "stacking", r.getProcess(), r.getMetric(), stacking, r.getActualValue()));
                    //System.out.println(new EnsembleTSVRow(resultNumber, "bagging", r.getProcess(), r.getMetric(), bagging, r.getActualValue()));
                 }

                //System.out.println();
            }


        }

/*        Collections.sort(rows);

        computeBagging(rows);

        for (EnsembleTSVRow row : rows) {
            System.out.println(row);
        }*/
    }

    private static double accuracy(double actual, double predicted) {
        return Math.max( Math.abs(predicted-actual)/actual, 1 );
    }

    private static Map<Integer, List<ModelResult>> splitByResultNumber(List<ModelResult> theModelResults) {
        Map <Integer, List<ModelResult>> map = new LinkedHashMap<>();
        for (ModelResult result : theModelResults) {

            Integer key = result.getResultNumber();

            List<ModelResult> modelResults = map.containsKey(key) ? map.get(key) : new ArrayList<ModelResult>();
            modelResults.add(result);

            map.put(key, modelResults);
        }

        return map;
    }

    private static void computeBagging(List<EnsembleTSVRow> bRows) {
        Collections.sort(bRows);

    }

    private static Map<String, List<ModelResult>> splitByProcesMetric(List<ModelResult> theModelResults) {

        Map <String, List<ModelResult>> map = new LinkedHashMap<>();
        for (ModelResult result : theModelResults) {

            String key = result.getProcess()+result.getMetric();

            List<ModelResult> modelResults = map.containsKey(key) ? map.get(key) : new ArrayList<ModelResult>();
            modelResults.add(result);

            map.put(key, modelResults);
        }

        return map;
    }
}
