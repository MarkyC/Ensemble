package ca.yorku.cirillom.ensemble.models;

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

                    r.setResultNumber(resultNumber);
                    r.setModeller(model.getClass().getSimpleName());
                    this.notifyListeners("model-result", r, r);
                }
                //this.notifyListeners(model.getClass().getSimpleName(), result, result);
            }
        }

        this.notifyListeners(FINISHED, false, true);

    }

/*    @Override
    public void run() {

        for ( Iterator<DataValue> it = performanceData.getDataValues().iterator(); it.hasNext(); ) {
            DataValue dataValue = it.next();

            for (Map.Entry<String, IEnsembleModel> entry : models.entrySet()) {
                String name         = entry.getKey();
                IEnsembleModel model= entry.getValue();

                model.addInput(dataValue);
                model.model();
                //this.notifyListeners(name, model.getResults(), model.model());
            }
        }

        for (Map.Entry<String, IEnsembleModel> entry : models.entrySet()) {
            String name         = entry.getKey();
            IEnsembleModel model= entry.getValue();


            int[] toPredict = {10, 25, 50, 100};
            for (int workload : toPredict) {
                this.notifyListeners(name, model.getResults(), model.predict(workload));
            }
        }

        while ( !this.isInterrupted() ) {

            // cat nap
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (0 != this.nextWorkload) {

                System.out.println("next workload: " +nextWorkload);

                for (Map.Entry<String, IEnsembleModel> entry : models.entrySet()) {
                    String name         = entry.getKey();
                    IEnsembleModel model= entry.getValue();

                    this.notifyListeners(name, model.getResults(), model.predict(this.nextWorkload));
                }

                this.nextWorkload = 0;
            }
        }
    }*/
}
