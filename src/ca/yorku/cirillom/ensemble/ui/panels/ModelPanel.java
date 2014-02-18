package ca.yorku.cirillom.ensemble.ui.panels;

import ca.yorku.cirillom.ensemble.models.IEnsembleModel;
import ca.yorku.cirillom.ensemble.models.ModelEnsemble;
import ca.yorku.cirillom.ensemble.models.ModelResult;
import ca.yorku.cirillom.ensemble.models.PerformanceData;
import ca.yorku.cirillom.ensemble.preferences.Preferences;
import ca.yorku.cirillom.ensemble.ui.MainWindow;
import ca.yorku.cirillom.ensemble.util.Util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

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
    List<PerformanceData> data;

    private final JButton startButton;

    public ModelPanel(final MainWindow parent) {
        super();
        this.parent = parent;
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(Util.createBorder(TITLE));

        startButton = new JButton(NO_FILE);
        startButton.setEnabled(false);
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                for (final PerformanceData d : data) {
                    for (final String metric : d.getMetrics()) {
                        ModelEnsemble ensemble = new ModelEnsemble(d.get(metric));
                        ensemble.addPropertyChangeListener(new PropertyChangeListener() {

                            @Override
                            public void propertyChange(PropertyChangeEvent evt) {

                                IEnsembleModel model = (IEnsembleModel) evt.getNewValue();
                                ModelPanel.this.firePropertyChange("result", null, new ModelResult(
                                        d.getProcess(),
                                        metric,
                                        model.getLastValue(),
                                        model.getLastResult(),
                                        model.getAccuracy()
                                ));
                                /*System.out.println(
                                        d.getProcess() + " " + metric + ": (" +
                                                model.getLastValue() + ", " +
                                                model.getLastResult() + ", " +
                                                model.getAccuracy() + ")");*/
                            }
                        });
                        ensemble.start();
                    }
                }
            }
        });

        JCheckBox movingAverage = new JCheckBox("Moving Average");
        movingAverage.setSelected(
                Preferences.getInstance().get(Preferences.MODELS).contains(Preferences.MOVING_AVERAGE));

        //JPanel container = new JPanel();
        //container.setLayout()

        this.add(movingAverage);
        this.add(startButton);

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
