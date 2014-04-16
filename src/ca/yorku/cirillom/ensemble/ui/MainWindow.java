package ca.yorku.cirillom.ensemble.ui;

import ca.yorku.cirillom.ensemble.models.ModelEnsemble;
import ca.yorku.cirillom.ensemble.models.ModelResult;
import ca.yorku.cirillom.ensemble.models.PerformanceData;
import ca.yorku.cirillom.ensemble.preferences.Preferences;
import ca.yorku.cirillom.ensemble.ui.panels.InputPanel;
import ca.yorku.cirillom.ensemble.ui.panels.ModelPanel;
import ca.yorku.cirillom.ensemble.ui.panels.ResultPanel;
import ca.yorku.cirillom.ensemble.util.EnsembleTSVWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 8:22 PM.
 * Main application window
 */
public class MainWindow implements PropertyChangeListener {

    /**
     * Window title.
     */
    public static final String TITLE = "Ensemble Model Solver";

    private JFrame frame;
    private InputPanel inputPanel = new InputPanel();
    private ModelPanel modelPanel = new ModelPanel();
    private ResultPanel resultPanel = new ResultPanel();

    private EnsembleTSVWriter output = new EnsembleTSVWriter();

    public MainWindow() {

        // Initialize Window
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Add this window as a listener for it's child panels
        inputPanel.addPropertyChangeListener(this);
        modelPanel.addPropertyChangeListener(this);
        resultPanel.addPropertyChangeListener(this);

        // Create supporting panels
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(inputPanel);
        centerPanel.add(modelPanel);
        //centerPanel.add(resultPanel);

        // Add panels to the JFrame's contentPane
        Container c = frame.getContentPane();
        c.add(centerPanel, BorderLayout.CENTER);
    }

    public void show() {
        frame.pack();
        frame.setVisible(true);
    }

    public void addWindowListener(WindowListener l) {
        frame.addWindowListener(l);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (InputPanel.FILE_LOADED.equalsIgnoreCase(event.getPropertyName())) {
            PerformanceData result = inputPanel.getPerformanceData();
            modelPanel.setPerformanceData(result);
            modelPanel.setEnabled(true);
        } else if (Preferences.getInstance().getAsList(Preferences.ENABLED_MODELS).contains(event.getPropertyName()) &&
                event.getNewValue() instanceof List) {
            List<ModelResult> result = (List<ModelResult>) event.getNewValue();

            // Update the GUI
            resultPanel.updateResult( event.getPropertyName(), result );

            // Update the ouput TSV
            //output.addModelResults(result);

        } else if (ModelEnsemble.FINISHED.equals(event.getPropertyName())) {
            output.write();
        } else if ("model-result".equals(event.getPropertyName())) {
            // Update the ouput TSV
            output.addModelResult((ModelResult) event.getNewValue());
        }
    }
}
