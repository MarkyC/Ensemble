package ca.yorku.cirillom.ensemble.ui.panels;

import ca.yorku.cirillom.ensemble.models.ModelResult;
import ca.yorku.cirillom.ensemble.ui.MainWindow;
import ca.yorku.cirillom.ensemble.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

    JTable table/* = new JTable()*/;

    public ResultPanel(final MainWindow parent) {
        super();
        this.parent = parent;
        this.setBorder(Util.createBorder(TITLE));
        this.setLayout(new BorderLayout());

        String[] columnNames = {
                "Modeller",
                "Process",
                "Metric",
                "Workload",
                "Actual Value",
                "Computed Value",
                "Error%"};

        table = new JTable(new DefaultTableModel(new Object[][]{}, columnNames));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        this.add(scrollPane, BorderLayout.CENTER);

    }

    private String makePercent(double val) {
        double hundred = val * 100;
        return hundred + "%";
    }

    public void updateResult(String modelName, List<ModelResult> results) {
        DefaultTableModel m = (DefaultTableModel) table.getModel();

        //System.out.println(modelName + " " + result);

        for (ModelResult result : results) {
            boolean found = false;

            /*for (int i = 0; i < m.getRowCount(); i++) {
                if ( (m.getValueAt(i, 0).equals(modelName) &&
                        m.getValueAt(i, 1).equals(result.getProcess())) &&
                        (m.getValueAt(i, 2).equals(result.getMetric())) ) {

                    m.setValueAt(result.getTotalWorkload(),                  i, 4);
                    m.setValueAt(result.getActualValue(),               i, 4);
                    m.setValueAt(result.getComputedValue(),             i, 5);
                    m.setValueAt(makePercent(result.getErrorPercent()), i, 6); // multiply by 100 for percent
                    found = true;

                }
            }*/

            if (!found) {
                m.addRow(new Object[] {
                        modelName,
                        result.getProcess(),
                        result.getMetric(),
                        result.getWorkload(),
                        result.getActualValue(),
                        result.getComputedValue(),
                        result.getErrorPercent()+"%"});
            }
        }
    }
}
