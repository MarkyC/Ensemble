package ca.yorku.cirillom.ensemble.ui;

import ca.yorku.cirillom.ensemble.models.ModelResult;
import ca.yorku.cirillom.ensemble.models.PerformanceData;
import ca.yorku.cirillom.ensemble.preferences.Preferences;
import ca.yorku.cirillom.ensemble.ui.panels.InputPanel;
import ca.yorku.cirillom.ensemble.ui.panels.ModelPanel;
import ca.yorku.cirillom.ensemble.ui.panels.ResultPanel;

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
    private InputPanel inputPanel;
    private ModelPanel modelPanel;
    private ResultPanel resultPanel;

    public MainWindow() {
        init();
    }

    private void init() {
        // Initialize Window
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create supporting panels
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        inputPanel = new InputPanel(this);
        inputPanel.addPropertyChangeListener(this);
        centerPanel.add(inputPanel);

        modelPanel = new ModelPanel();
        modelPanel.addPropertyChangeListener(this);
        centerPanel.add(modelPanel);

        resultPanel = new ResultPanel(this);
        resultPanel.addPropertyChangeListener(this);
        centerPanel.add(resultPanel);

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
        if ("fileLoaded".equalsIgnoreCase(event.getPropertyName())) {
            PerformanceData result = inputPanel.getPerformanceData();
            modelPanel.setPerformanceData(result);
            modelPanel.setEnabled(true);
        } else if (Preferences.getInstance().getAsList(Preferences.ENABLED_MODELS).contains(event.getPropertyName()) &&
                event.getNewValue() instanceof List) {
            resultPanel.updateResult(event.getPropertyName(), (List<ModelResult>) event.getNewValue());
        }
    }
}
