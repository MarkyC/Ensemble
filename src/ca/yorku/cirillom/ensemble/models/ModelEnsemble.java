package ca.yorku.cirillom.ensemble.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/17/14 7:27 PM.
 */
public class ModelEnsemble extends Thread  {

    private final PerformanceData performanceData;

    private Map<String, IEnsembleModel> models = new LinkedHashMap<>();
    /*public void addModel(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (availableModels.containsKey(name)) {
            ensemble.put(name, (IEnsembleModel) availableModels.get(name).newInstance());
        } else {
            throw new ClassNotFoundException("Model for " + name + " not found.");
        }
    }*/

    private Map<String, IEnsembleModel> ensemble = new LinkedHashMap<>();
    /*public void addModelToEnsemble(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (availableModels.containsKey(name)) {
            ensemble.put(name, (IEnsembleModel) availableModels.get(name).newInstance());
        } else {
            throw new ClassNotFoundException("Model for " + name + " not found.");
        }
    }*/

    private List<PropertyChangeListener> listeners = new ArrayList<>();
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }
    public void notifyListeners(String propertyName, List<ModelResult> previousResult, List<ModelResult> result) {
        for (PropertyChangeListener p : listeners) {
            p.propertyChange(new PropertyChangeEvent(this, propertyName, previousResult, result));
        }
    }

    /**
     * List of Models this ModelEnsemble Supports
     */
    /*public static final Map<String, Class> availableModels = new LinkedHashMap<>();
    static {
        availableModels.put(Preferences.MOVING_AVERAGE, MovingAverageModel.class);
        availableModels.put(Preferences.LINEAR_REGRESSION, LinearRegressionModel.class);
    }*/

    /*public ModelEnsemble() {
        this(new ArrayList<DataValue>());
    }

    public ModelEnsemble(List<DataValue> data) {

        // Initialize with the availableModels and the passed input
        this(data, availableModels.keySet().toArray(new String[1]));
    }*/

    public ModelEnsemble(PerformanceData perfData, String[] models) throws ClassNotFoundException {
        this.performanceData = perfData;//new ArrayList<>(data);
        for ( String model : models ) {
            //addModel(model);
            switch(model.trim()) {
                case "moving-average":
                    this.models.put("moving-average", new MovingAverageModel());
                break;
                case "linear-regression":
                    this.models.put("linear-regression", new LinearRegressionModel());
                break;
                default:
                    throw new ClassNotFoundException("Cannot find class " + model);
            }
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

        //int offset  = 0;

        // loop until interrupted
        //while ( !this.isInterrupted() ) {

            // cat nap
            /*try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            for ( Iterator<DataValue> it = performanceData.getDataValues().iterator(); it.hasNext(); ) {
                DataValue dataValue = it.next();

                for (Map.Entry<String, IEnsembleModel> entry : models.entrySet()) {
                    String name         = entry.getKey();
                    IEnsembleModel model= entry.getValue();

                    model.addInput(dataValue);
                    this.notifyListeners(name, model.getResults(), model.model());
                }
            }



            /*for (Map.Entry<String, IEnsembleModel> entry : models.entrySet()) {
                String name         = entry.getKey();
                IEnsembleModel model= entry.getValue();

                if (offset > data.size() - 1) {

                    // we've exhausted the input input, close the model solver
                    System.out.println("Input exhausted, ending ensemble");
                    this.interrupt();

                } else {

                    // get the latest input value and add to the model
                    DataValue value = data.get(offset);
                    model.addInput(value);
                }

                model.model();
                this.notifyListeners(name, model);
            }*/

/*
                // addInput DataValue to all models
                for (String modeller : models.keySet())     models.get(modeller).addInput(value);
                for (String modeller : ensemble.keySet())   ensemble.get(modeller).addInput(value);


                // compute next value for all models
                for (String modeller : models.keySet())     models.get(modeller).model();
                for (String modeller : ensemble.keySet())   ensemble.get(modeller).model();

                // Fire off listeners
                for (String modeller : models.keySet())     this.notifyListeners(models.get(modeller));
                for (String modeller : ensemble.keySet())   this.notifyListeners(ensemble.get(modeller));

                // If accuracy for any model is higher than all in the ensemble, addInput to ensemble
                for (Iterator<Map.Entry<String, IEnsembleModel>> iterator = models.entrySet().iterator(); iterator.hasNext(); ) {

                    Map.Entry<String, IEnsembleModel> entry = iterator.next();

                    String metric           = entry.getKey();
                    IEnsembleModel model    = entry.getValue();
                    boolean addToEnsemble   = true;

                    for (IEnsembleModel e : ensemble.values()) {
                        if (model.getError() < e.getError() ) {
                            addToEnsemble = false;
                        }
                    }

                    if (addToEnsemble) {
                        ensemble.put(metric, model);
                        iterator.remove();
                    }
                } // End addInput to ensemble if accuracy is higher
*/

            //++offset; // increment offset for next iteration
        //} // end while
    }
}
