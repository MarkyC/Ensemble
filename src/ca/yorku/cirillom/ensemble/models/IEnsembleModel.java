package ca.yorku.cirillom.ensemble.models;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/17/14 7:22 PM.
 */
public interface IEnsembleModel {

    /**
     * Adds a DataValue into this Modeller
     * @param data the DataValue to add
     */
    public void add(DataValue data);

    /**
     * Runs this modeller on the set of all DataValues
     * @return the latest computed result
     */
    public double model();

    /**
     * Returns the latest computed result
     * @return the latest computed result
     */
    public double getResult();

    /**
     * Returns the second latest computed result. This is separate from getResult() because this result
     * can be compared to gathered data to compute this modellers accuracy
     * @return the second latest computed result
     */
    public double getLastResult();

    /**
     * Returns the latest added DataValue's value
     * @return the latest added DataValue's value
     */
    public double getLastValue();

    /**
     * Returns the most recently computed accuracy
     * @return the most recently computed accuracy
     */
    public double getAccuracy();
}
