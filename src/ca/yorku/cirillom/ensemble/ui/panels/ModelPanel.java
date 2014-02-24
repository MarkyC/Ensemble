package ca.yorku.cirillom.ensemble.ui.panels;

import ca.yorku.cirillom.ensemble.models.*;
import ca.yorku.cirillom.ensemble.preferences.Preferences;
import ca.yorku.cirillom.ensemble.ui.MainWindow;
import ca.yorku.cirillom.ensemble.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 8:35 PM.
 * Handles File Input or live collection
 */
public class ModelPanel extends JPanel {

    public static final String TITLE = "Model Ensemble";
    public static final String NO_FILE = "Please select a file first";

    private final MainWindow parent;

    private List<PerformanceData> data          = new ArrayList<>();
    private final List<JCheckBox> checkboxes    = new ArrayList<>();

    private final JButton startButton;

    public ModelPanel(final MainWindow parent) {
        super();
        this.parent = parent;
        this.setLayout(new BorderLayout ());
        this.setBorder(Util.createBorder(TITLE));

        startButton = new JButton(NO_FILE);
        startButton.setEnabled(false);
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                for (JCheckBox box : checkboxes) box.setEnabled(false);

                for (Iterator<PerformanceData> performanceDataIterator = data.iterator(); performanceDataIterator.hasNext();) {

                    final PerformanceData perfData = performanceDataIterator.next();

                    for (Map.Entry<String, List<DataValue>> entry : perfData.getMetrics().entrySet()) {

                        final String metric             = entry.getKey();
                        final List<DataValue> values    = entry.getValue();

                        ModelEnsemble ensemble = null;
                        try {
                            ensemble = new ModelEnsemble(values);

                            ensemble.addPropertyChangeListener(new PropertyChangeListener() {

                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {

                                    IEnsembleModel model = (IEnsembleModel) evt.getNewValue();
                                    ModelPanel.this.firePropertyChange(evt.getPropertyName(), null, new ModelResult(
                                            perfData.getProcess(),
                                            metric,
                                            model.getLastInput().getValue(),
                                            model.getLastPrediction(),
                                            model.getError()
                                    ));
                                }
                            });
                            ensemble.start();
                        } catch (ClassNotFoundException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        for (String modelName : Preferences.getInstance().get(Preferences.ALL_MODELS).split(",")) {
            final JCheckBox box = new JCheckBox(modelName);

            box.setName(modelName);

            // Set selected if the model is in the list of enabled models
            box.setSelected(Preferences.getInstance()
                    .get(Preferences.ENABLED_MODELS)
                    .contains(modelName));

            box.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if (((JCheckBox) e.getSource()).isSelected()) {
                        // Add to enabled model preference
                        Preferences.getInstance().append(Preferences.ENABLED_MODELS, box.getName());
                    } else {
                        // remove from enabled model preference
                        Preferences.getInstance().removeFromKey(Preferences.ENABLED_MODELS, box.getName());
                    }

                }
            });

            checkboxes.add(box);

        }


        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        for (JCheckBox box : checkboxes) container.add(box);
        //container.add(movingAverage);
        container.add(startButton);
        this.add(container);

    }

    public void setData(List<PerformanceData> data) {
        this.data = data;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        startButton.setEnabled(true);
        startButton.setText("Start");
    }

}
