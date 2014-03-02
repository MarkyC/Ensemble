package ca.yorku.cirillom.ensemble.ui.panels;

import ca.yorku.cirillom.ensemble.models.PerformanceData;
import ca.yorku.cirillom.ensemble.ui.MainWindow;
import ca.yorku.cirillom.ensemble.ui.util.ProgressWindow;
import ca.yorku.cirillom.ensemble.util.InputFileFilter;
import ca.yorku.cirillom.ensemble.util.TSVParser;
import ca.yorku.cirillom.ensemble.util.Util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

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
    private final MainWindow parent;
    List<PerformanceData> data;

    public InputPanel(final MainWindow parent) {
        super();
        this.parent = parent;
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
                    File file = fileChooser.getSelectedFile();
                    switch(Util.getExtension(file)) {
                        case "tsv":

                            TSVParser p = new TSVParser(file, new ProgressWindow());

                            InputPanel.this.addPropertyChangeListener(InputPanel.this.parent);
                            p.addPropertyChangeListener(new PerfChange(p));
                            p.execute();
                        break;
                    }

                    fileLabel.setText(file.getName());
                }
            }
        });


        this.add(fileLabel);
        this.add(openFile);
    }

    class PerfChange implements PropertyChangeListener {

        private final TSVParser parser;

        public PerfChange(TSVParser parser) { this.parser = parser; }
        @Override
        public void propertyChange(final PropertyChangeEvent event) {

            if ("finished" == event.getPropertyName()) {
                data = parser.getResult();
                InputPanel.this.firePropertyChange("finished", false, true);
            }
        }
    }

    public List<PerformanceData> getData() {
        return this.data;
    }
}
