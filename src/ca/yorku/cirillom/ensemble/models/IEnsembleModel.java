package ca.yorku.cirillom.ensemble.models;

import java.util.List;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/17/14 7:22 PM.
 */
public interface IEnsembleModel {

    /**
     * Adds a DataValue into this Modeller
     * @param input the DataValue to addInput
     */
    public void addInput(DataValue input);

    /**
     * Adds a List of DataValue's into this Modeller
     * @param input the DataValue to addInput
     */
    public void addInput(List<DataValue> input);

    /**
     * Returns the latest added DataValue's value
     * @return the latest added DataValue's value
     */
    public DataValue getLastInput() throws NullPointerException;

    /**
     * Runs this modeller on the set of all DataValues
     * @return the latest computed result
     */
    public double model();

    /**
     * Returns the latest computed result
     * @return the latest computed result
     */
    public double getNextPrediction();

    /**
     * Returns the second latest computed result. This is separate from getNextPrediction() because this result
     * can be compared to gathered input to compute this modellers accuracy
     * @return the second latest computed result
     */
    public double getLastPrediction();

    /**
     * Returns the most recently computed accuracy
     * @return the most recently computed accuracy
     */
    public double getError();
}
