package ca.yorku.cirillom.ensemble.ui.panels;

import ca.yorku.cirillom.ensemble.models.ModelResult;
import ca.yorku.cirillom.ensemble.models.PerformanceData;
import ca.yorku.cirillom.ensemble.ui.MainWindow;
import ca.yorku.cirillom.ensemble.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 8:35 PM.
 * Handles File Input or live collection
 */
public class ResultPanel extends JPanel {

    public static final String TITLE = "Results";

    public static final String NO_FILE = "Please select a file first";
    private final MainWindow parent;
    List<PerformanceData> data;

    JTable table;

    public ResultPanel(final MainWindow parent) {
        super();
        this.parent = parent;
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(Util.createBorder(TITLE));

        String[] columnNames = {"Process",
                "Metric",
                "Actual Value",
                "Computed Value",
                "Accuracy"};

        table = new JTable(new DefaultTableModel(new Object[][]{}, columnNames));

        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        this.add(scrollPane);

    }

    public void updateResult(ModelResult result) {
        DefaultTableModel m = (DefaultTableModel) table.getModel();

        boolean found = false;

        for (int i = 0; i < m.getRowCount(); i++) {
            if ( (m.getValueAt(i, 0).equals(result.getProcess())) &&
                    (m.getValueAt(i, 1).equals(result.getMetric())) ) {
                m.setValueAt(result.getActualValue(),   i, 2);
                m.setValueAt(result.getComputedValue(), i, 3);
                m.setValueAt(result.getErrorPercent() + "%",      i, 4);
                found = true;
            }
        }

        if (!found) {
            m.addRow(new Object[] {
                    result.getProcess(),
                    result.getMetric(),
                    result.getActualValue(),
                    result.getComputedValue(),
                    result.getErrorPercent()+"%"});
        }
    }
}
