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

    private Map<String, IEnsembleModel> models;
    private Map<String, IEnsembleModel> ensemble;
    private List<PropertyChangeListener> listeners;

    public ModelEnsemble(List<DataValue> data) {
        this.data = data;

        models   = new HashMap<>();
        ensemble = new HashMap<>();
        listeners= new ArrayList<>();
    }

    @Override
    public void run() {

        int offset = 0;

        // populate models from Preferences
        for ( String model : Preferences.getInstance().get(Preferences.MODELS).split(",") ) {
            switch (model) {
                case Preferences.MOVING_AVERAGE:
                    System.out.println("putting");
                    models.put(model, new MovingAverageModel());
                break;
            }
        }

        MovingAverageModel theModel = new MovingAverageModel();

        // loop until interrupted
        while (!this.isInterrupted()) {

            // cat nap
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (offset > data.size() - 1) {

                // we've exhausted the input data, close the model solver
                System.out.println("Input data exhausted, ending ensemble");
                this.interrupt();

            } else {

                // get the latest data value
                DataValue value = data.get(offset);

                theModel.add(value);
                theModel.model();
                this.notifyListeners(theModel);

/*
                // add DataValue to all models
                for (String modeller : models.keySet())     models.get(modeller).add(value);
                for (String modeller : ensemble.keySet())   ensemble.get(modeller).add(value);


                // compute next value for all models
                for (String modeller : models.keySet())     models.get(modeller).model();
                for (String modeller : ensemble.keySet())   ensemble.get(modeller).model();

                // Fire off listeners
                for (String modeller : models.keySet())     this.notifyListeners(models.get(modeller));
                for (String modeller : ensemble.keySet())   this.notifyListeners(ensemble.get(modeller));

                // If accuracy for any model is higher than all in the ensemble, add to ensemble
                for (Iterator<Map.Entry<String, IEnsembleModel>> iterator = models.entrySet().iterator(); iterator.hasNext(); ) {

                    Map.Entry<String, IEnsembleModel> entry = iterator.next();

                    String metric           = entry.getKey();
                    IEnsembleModel model    = entry.getValue();
                    boolean addToEnsemble   = true;

                    for (IEnsembleModel e : ensemble.values()) {
                        if (model.getAccuracy() < e.getAccuracy() ) {
                            addToEnsemble = false;
                        }
                    }

                    if (addToEnsemble) {
                        ensemble.put(metric, model);
                        iterator.remove();
                    }
                } // End add to ensemble if accuracy is higher
*/
            }
            ++offset; // increment offset for next iteration
        } // end while
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }

    public void notifyListeners(IEnsembleModel model) {
        for (PropertyChangeListener p : listeners) {
            p.propertyChange(new PropertyChangeEvent(this, "moving-average", null, model));
        }
    }
}
