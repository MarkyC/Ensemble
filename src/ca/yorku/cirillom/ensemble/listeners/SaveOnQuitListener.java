package ca.yorku.cirillom.ensemble.listeners;

import ca.yorku.cirillom.ensemble.preferences.Preferences;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 8:17 PM.
 *
 * Saves User Preferences on Window Closing
 */
public class SaveOnQuitListener implements WindowListener {

    /**
     * Saves User Preferences
     * @param e
     */
    @Override
    public void windowClosing(WindowEvent e) {
        Preferences prefs = Preferences.getInstance();
        prefs.save();
    }

    @Override public void windowOpened(WindowEvent e) { }

    @Override public void windowClosed(WindowEvent e) { }

    @Override public void windowIconified(WindowEvent e) { }

    @Override public void windowDeiconified(WindowEvent e) { }

    @Override public void windowActivated(WindowEvent e) { }

    @Override public void windowDeactivated(WindowEvent e) { }
}
