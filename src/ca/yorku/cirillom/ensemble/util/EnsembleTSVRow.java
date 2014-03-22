package ca.yorku.cirillom.ensemble.util;

import ca.yorku.cirillom.ensemble.models.ModelResult;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 3/21/2014 12:42 PM.
 * <p/>
 * Description
 * <p/>
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 */
public class EnsembleTSVRow implements Comparable<EnsembleTSVRow> {

    private final int resultNumber;
    public String ensembleType;
    public String process;
    public String metric;
    public double prediction;
    public double actual;

    public EnsembleTSVRow(String ensembleType, ModelResult result) {
        this(
                result.getResultNumber(),
                ensembleType,
                result.getProcess(),
                result.getMetric(),
                result.getComputedValue(),
                result.getActualValue()
        );
    }

    public EnsembleTSVRow(int resultNumber, String ensembleType, String process, String metric, double prediction, double actual) {
        this.resultNumber   = resultNumber;
        this.ensembleType   = ensembleType;
        this.process        = process;
        this.metric         = metric;
        this.prediction     = prediction;
        this.actual         = actual;
    }

    public int getResultNumber() {
        return resultNumber;
    }

    public String getEnsembleType() {
        return ensembleType;
    }

    public String getProcess() {
        return process;
    }

    public String getMetric() {
        return metric;
    }

    public double getPrediction() {
        return prediction;
    }

    public double getActual() {
        return actual;
    }

    @Override
    public int compareTo(EnsembleTSVRow o) {

        // Sort by Ensemble Type
        int comparison = this.getEnsembleType().compareTo(o.getEnsembleType());
        if ( 0 != comparison ) return comparison;

        // Subsort by Process
        comparison = this.getProcess().compareTo(o.getProcess());
        if ( 0 != comparison ) return comparison;

        // Subsort by Metric
        comparison = this.getMetric().compareTo(o.getMetric());
        if ( 0 != comparison ) return comparison;

        // Subsort by window
        if (this.getResultNumber() > o.getResultNumber()) {
            return 1;
        } else if (this.getResultNumber() < o.getResultNumber()) {
            return -1;
        }

        // They are considered the same at this point
        return 0;
    }

    @Override
    public String toString() {
        return this.getResultNumber() + "\t" + this.getEnsembleType() + "\t" + this.getProcess() + "\t" + this.getMetric() + "\t" + this.getPrediction() + "\t" + this.getActual();
    }
}
