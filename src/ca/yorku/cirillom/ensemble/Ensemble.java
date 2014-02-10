package ca.yorku.cirillom.ensemble;

import ca.yorku.cirillom.ensemble.listeners.SaveOnQuitListener;
import ca.yorku.cirillom.ensemble.ui.MainWindow;

import javax.swing.*;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 6:43 PM.
 */
public class Ensemble {

    public static void main(String[] args) {

        // Load window
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainWindow window = new MainWindow();
                window.addWindowListener(new SaveOnQuitListener());
                window.show();
            }
        });
    }
}
