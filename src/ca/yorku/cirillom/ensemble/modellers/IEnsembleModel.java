package ca.yorku.cirillom.ensemble.modellers;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.ModelResult;

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
     * Returns the latest added DataValue's value
     * @return the latest added DataValue's value
     */
    public DataValue getLastInput();

    /**
     * Returns the latest computed result
     * @return the latest computed result
     */
    public List<ModelResult> getResults();

    /**
     * Attempts to predict the resource usage based on the given workload
     * @param dataValue - the datavalue to pull workloads (number of users) from
     * @return A List of ModelResults, one for each metric
     */
    public List<ModelResult> predict(DataValue dataValue);

}
