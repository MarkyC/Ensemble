package ca.yorku.cirillom.ensemble.models;

import java.util.List;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 11:10 PM.
 * Holds a list of DataValues for each metric the PerformanceData is watching
 */
public class PerformanceData {

    private List<DataValue> dataValues;

    public PerformanceData(List<DataValue> dataValues) {
        this.dataValues = dataValues;
    }

    public List<DataValue> getDataValues() {
        return dataValues;
    }

    public boolean addDataValue(DataValue v) {
        return dataValues.add(v);
    }
}
