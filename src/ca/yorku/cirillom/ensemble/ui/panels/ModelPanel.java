package ca.yorku.cirillom.ensemble.ui.panels;

import ca.yorku.cirillom.ensemble.models.ModelEnsemble;
import ca.yorku.cirillom.ensemble.models.PerformanceData;
import ca.yorku.cirillom.ensemble.preferences.Preferences;
import ca.yorku.cirillom.ensemble.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 8:35 PM.
 * Handles File Input or live collection
 */
public class ModelPanel extends JPanel {

    public static final String ENSEMBLE_TITLE = "Model Ensemble";
    public static final String QUERY_TITLE = "Query Results (enter a workload)";
    public static final String NO_FILE = "Please select a file first";

    private PerformanceData performanceData;
    public void setPerformanceData(PerformanceData data) {
        this.performanceData = data;
    }

    private final List<JCheckBox> checkboxes    = new ArrayList<>();

    private final JButton startButton;

    private ModelEnsemble ensemble;

    public ModelPanel() {
        super();
        this.setLayout(new BorderLayout());

        startButton = new JButton(NO_FILE);
        startButton.setEnabled(false);
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    ensemble = new ModelEnsemble(performanceData,
                                    Preferences.getInstance().getAsArray(Preferences.ENABLED_MODELS));
                    ensemble.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {

                            ModelPanel.this.firePropertyChange(evt.getPropertyName(), null, evt.getNewValue());

                        }
                    });
                    ensemble.start();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Set up Checkboxes
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

        final JTextField workloadField = new JTextField(20);
        JButton query = new JButton("Query");
        query.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = workloadField.getText();
                int workload = Integer.parseInt(input);
                ensemble.setNextWorkload(workload);
            }
        });


        JPanel queryPanel = new JPanel();
        queryPanel.setBorder(Util.createBorder(QUERY_TITLE));
        queryPanel.add(workloadField);
        queryPanel.add(query);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        for (JCheckBox box : checkboxes) container.add(box);
        //container.add(movingAverage);
        container.add(startButton);

        JPanel ensemblePanel = new JPanel();
        ensemblePanel.setLayout(new BorderLayout ());
        ensemblePanel.setBorder(Util.createBorder(ENSEMBLE_TITLE));
        ensemblePanel.add(container);

        this.add(ensemblePanel, BorderLayout.CENTER);
        this.add(queryPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        startButton.setEnabled(true);
        startButton.setText("Start");
    }

}
