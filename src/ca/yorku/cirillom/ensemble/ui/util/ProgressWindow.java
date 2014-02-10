package ca.yorku.cirillom.ensemble.ui.util;

import javax.swing.*;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/10/14 1:23 PM.
 */
public class ProgressWindow extends JFrame {

    private JProgressBar bar;

    public ProgressWindow() {
        super("Processing...");

        bar = new JProgressBar();
        bar.setStringPainted(true);

        this.getContentPane().add(bar);

        this.pack();
        this.setVisible(true);
    }

    public void set(int progress) {
        bar.setValue(progress);
        bar.setString("" + progress + "%");
    }
}
