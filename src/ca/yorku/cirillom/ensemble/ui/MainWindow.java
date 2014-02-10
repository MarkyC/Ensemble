package ca.yorku.cirillom.ensemble.ui;

import ca.yorku.cirillom.ensemble.ui.panels.InputPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowListener;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 8:22 PM.
 * Main application window
 */
public class MainWindow {

    /**
     * Window title.
     */
    public static final String TITLE = "Ensemble Model Solver";

    private JFrame frame;

    public MainWindow() {
        init();
    }

    private void init() {
        // Initialize Window
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create supporting panels
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(new InputPanel(this));
        //centerPanel.add(new ModelPanel());

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
}
