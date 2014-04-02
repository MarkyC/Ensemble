package ca.yorku.cirillom.ensemble.models;

import ca.yorku.cirillom.ensemble.modellers.IEnsembleModel;
import ca.yorku.cirillom.ensemble.modellers.LinearRegressionModel;
import ca.yorku.cirillom.ensemble.modellers.MovingAverageModel;
import ca.yorku.cirillom.ensemble.preferences.Preferences;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/17/14 7:27 PM.
 */
public class ModelEnsemble extends Thread  {

    public static final String FINISHED         = "finished";
    public static final String ENSEMBLE_RESULT  = "ensemble-result";
    private final PerformanceData performanceData;

    private List<IEnsembleModel> models = new ArrayList<>();

    //private Map<String, IEnsembleModel> models = new LinkedHashMap<>();
    private Map<String, IEnsembleModel> ensemble = new LinkedHashMap<>();

    private List<PropertyChangeListener> listeners = new ArrayList<>();

    private int nextWorkload = 0;

    /**
     * Number of samples to run for each iteration of modelling.
     * After sampleWindow is hit, the ModelEnsemble will offer a prediction for the (sampleWindow + 1)'th result
     */
    public final int sampleWindow = 30;

    public void setNextWorkload(int nextWorkload) {
        this.nextWorkload = nextWorkload;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }
    public void notifyListeners(String propertyName, Object previousResult, Object result) {
        for (PropertyChangeListener p : listeners) {
            p.propertyChange(new PropertyChangeEvent(this, propertyName, previousResult, result));
        }
    }

    private IEnsembleModel getModelByName(String name) throws ClassNotFoundException {
        if ( name.equals(MovingAverageModel.class.getSimpleName()) ) {
            return new MovingAverageModel();
        } else if ( name.equals(LinearRegressionModel.class.getSimpleName()) ) {
            return new LinearRegressionModel();
        } else {
            throw new ClassNotFoundException("Could not find Model for " + name);
        }
    }

    public ModelEnsemble(PerformanceData perfData) throws RuntimeException {
        this.performanceData = perfData;

        // Add IEnsembleModels to models collection
        String[] rawModels = Preferences.getInstance().getAsArray(Preferences.ENABLED_MODELS);
        for (String modelName : rawModels) {
            try {
                models.add(getModelByName(modelName.trim()));
            } catch (ClassNotFoundException e) { /*  No need to fail if we're given one bad model... */ }
        }

        if ( 1 > models.size() ) {
            throw new RuntimeException("No valid models to run.");
        }

    }

    @Override
    public void start() {
        if (this.performanceData == null) {
            throw new IllegalStateException("Cannot start the ModelEnsemble without adding input. Use addData()");
        } else {
            super.start();
        }
    }

    @Override
    public void run() {


        List<DataValue> dataValues = performanceData.getDataValues();
        int size = dataValues.size();
        int resultNumber = 1;

        // Run the next `sampleWindow` amount of samples, stop when we run out of future samples tom compare against
        for (int offset = 0; (size - offset) > sampleWindow; offset += sampleWindow, resultNumber++) {

            // visit the next sampleWindow-many DataValues
            for (int i = 0; i < sampleWindow; i++) {
                DataValue value = dataValues.get(offset + i);

                // Add DataValue to each model
                for (IEnsembleModel model : models) {
                    model.addInput(value);
                }
            }


            // predict the next (sampleWindow+i'th) sample
            DataValue nextValue = dataValues.get(offset + sampleWindow + 1);
            for (IEnsembleModel model : models) {
                List<ModelResult> result = model.predict(nextValue.getWorkload());
                for (ModelResult r : result) {

                    // set the actual observed value
                    for (Metric m : nextValue.getMetrics()) {
                        if ((m.getProcess().equals(r.getProcess())) && m.getName().equals(r.getMetric())) {
                            r.setActualValue(m.getValue());
                        }
                    }
                    r.setResultNumber(resultNumber);
                    r.setModeller(model.getClass().getSimpleName());
                    this.notifyListeners("model-result", r, r);
                }
                //this.notifyListeners(model.getClass().getSimpleName(), result, result);
            }
        }

        this.notifyListeners(FINISHED, false, true);

    }
}
