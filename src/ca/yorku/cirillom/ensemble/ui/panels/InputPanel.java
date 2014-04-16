package ca.yorku.cirillom.ensemble.ui.panels;

import ca.yorku.cirillom.ensemble.models.PerformanceData;
import ca.yorku.cirillom.ensemble.ui.util.ProgressWindow;
import ca.yorku.cirillom.ensemble.util.InputFileFilter;
import ca.yorku.cirillom.ensemble.util.Util;
import ca.yorku.cirillom.ensemble.util.XMLParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 8:35 PM.
 * Handles File Input or live collection
 */
public class InputPanel extends JPanel {

    public static final String TITLE = "Data Input";

    public static final String SELECT_FILE = "Select an input file";
    public static final String BROWSE_FILE = "Browse...";
    public static final String NO_FILE = "No File Selected";
    public static final String FILE_LOADED = "fileloaded";
    PerformanceData performanceData;

    public InputPanel() {
        super();
        this.setBorder(Util.createBorder(TITLE));

        final JLabel fileLabel = new JLabel(NO_FILE);

        final JButton openFile = new JButton(BROWSE_FILE);
        openFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle(SELECT_FILE);
                fileChooser.setFileFilter(new InputFileFilter());

                if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(InputPanel.this)) {
                // User has selected a valid file

                    final File file = fileChooser.getSelectedFile();
                    final ProgressWindow progressWindow = new ProgressWindow();

                    switch(Util.getExtension(file)) {
                        /*case "tsv":

                            TSVParser p = new TSVParser(file, new ProgressWindow());

                            InputPanel.this.addPropertyChangeListener(InputPanel.this.parent);
                            p.addPropertyChangeListener(new PerfChange(p));
                            p.execute();
                        break;*/
                        case "xml":
                            final XMLParser xmlParser = new XMLParser(file);
                            xmlParser.addPropertyChangeListener(new PropertyChangeListener() {

                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {
                                    switch (evt.getPropertyName()) {

                                        case "progress":
                                            progressWindow.set((int) evt.getNewValue());
                                        break;

                                        case "state":
                                            if ( SwingWorker.StateValue.DONE.equals(evt.getNewValue()) ) {
                                                // Close progress window
                                                progressWindow.dispose();

                                                // Set performance Data
                                                InputPanel.this.performanceData = xmlParser.getResult();

                                                // Notify listeners they can grab our performance data
                                                InputPanel.this.firePropertyChange("fileLoaded", false, true);
                                            }
                                        break;

                                    }
                                }
                            });
                            xmlParser.execute();
                    }

                    fileLabel.setText(file.getName());
                }
            }
        });


        this.add(fileLabel);
        this.add(openFile);
    }

    /*class PerfChange implements PropertyChangeListener {

        private final TSVParser parser;

        public PerfChange(TSVParser parser) { this.parser = parser; }
        @Override
        public void propertyChange(final PropertyChangeEvent event) {

            if ("finished" == event.getPropertyName()) {
                performanceData = parser.getResult();
                InputPanel.this.firePropertyChange("finished", false, true);
            }
        }
    }*/

    public PerformanceData getPerformanceData() {
        return this.performanceData;
    }
}
