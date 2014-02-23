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

    private final List<DataValue> data;

    private Map<String, IEnsembleModel> models = new HashMap<>();
    public void addModel(String name) throws ClassNotFoundException {
        if (availableModels.containsKey(name)) {
            models.put(name, availableModels.get(name));
        } else {
            throw new ClassNotFoundException("Model for " + name + " not found.");
        }
    }

    private Map<String, IEnsembleModel> ensemble = new HashMap<>();
    public void addModelToEnsemble(String name) throws ClassNotFoundException {
        if (availableModels.containsKey(name)) {
            ensemble.put(name, availableModels.get(name));
        } else {
            throw new ClassNotFoundException("Model for " + name + " not found.");
        }
    }

    private List<PropertyChangeListener> listeners = new ArrayList<>();
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }
    public void notifyListeners(String propertyName, IEnsembleModel model) {
        for (PropertyChangeListener p : listeners) {
            p.propertyChange(new PropertyChangeEvent(this, "moving-average", null, model));
        }
    }

    /**
     * List of Models this ModelEnsemble Supports
     */
    public static final Map<String, IEnsembleModel> availableModels = new HashMap<>();
    static {
        availableModels.put(Preferences.MOVING_AVERAGE, new MovingAverageModel());
    }

    public ModelEnsemble() throws ClassNotFoundException {
        this(new ArrayList<DataValue>());
    }

    public ModelEnsemble(List<DataValue> data) throws ClassNotFoundException {

        // Initialize with the availableModels and the passed input
        this(data, availableModels.keySet().toArray(new String[1]));
    }

    public ModelEnsemble(List<DataValue> data, String[] models) throws ClassNotFoundException {
        this.data = data;//new ArrayList<>(data);
        for ( String model : models ) {
            addModel(model);
        }

    }

    @Override
    public void start() {
        if (1 > this.data.size()) {
            throw new IllegalStateException("Cannot start the ModelEnsemble without adding input. Use addData()");
        } else {
            super.start();
        }
    }

    @Override
    public void run() {

        int offset = 0;
/*
        // populate models from Preferences
        for ( String model : Preferences.getInstance().get(Preferences.ALL_MODELS).split(",") ) {
            switch (model) {
                case Preferences.MOVING_AVERAGE:
                    System.out.println("putting");
                    models.put(model, new MovingAverageModel());
                break;
            }
        }*/

        //MovingAverageModel theModel = new MovingAverageModel();

        // loop until interrupted
        while (!this.isInterrupted()) {

            // cat nap
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (offset > data.size() - 1) {

                // we've exhausted the input input, close the model solver
                System.out.println("Input input exhausted, ending ensemble");
                this.interrupt();

            } else {

                // get the latest input value
                DataValue value = data.get(offset);

                for (Map.Entry<String, IEnsembleModel> entry : models.entrySet()) {
                    String name         = entry.getKey();
                    IEnsembleModel model= entry.getValue();

                    model.addInput(value);
                    model.model();
                    this.notifyListeners(name, model);
                }

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
            }
            ++offset; // increment offset for next iteration
        } // end while
    }
}
