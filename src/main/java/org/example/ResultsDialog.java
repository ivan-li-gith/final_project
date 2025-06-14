package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 * A dialog window that displays the results of a planning poker voting round.
 * This view presents the vote distribution both as a text summary and as a
 * pie chart, and it shows the final calculated point value for the story.
 *
 * @author Aadi Dhanda
 * @version 1.0
 */

public class ResultsDialog extends JDialog {
    public ResultsDialog(Map<String, Integer> counts, double total) {
        // formatting for the results dialog
        setTitle("Voting Results");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(255, 255, 200));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Arial", Font.BOLD, 14));
        resultsArea.setBackground(content.getBackground());

        StringBuilder sb = new StringBuilder();
        sb.append("Voting Results:\n\n");
        counts.forEach((card, count) ->
                sb.append(String.format("%3s : %d votes\n", card, count)));
        sb.append("\nTotal Points: ").append(String.format("%.1f", total));

        resultsArea.setText(sb.toString());
        content.add(new JScrollPane(resultsArea), BorderLayout.CENTER);

        // showing the pie chart of the voting results
        DefaultPieDataset dataset = new DefaultPieDataset();
        counts.forEach((label, score) -> {
            dataset.setValue(label, score);
        });

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Voting Distribution", dataset, true, true, false
        );

        ChartPanel chartPanel = new ChartPanel(pieChart);
        content.add(chartPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        content.add(closeButton, BorderLayout.SOUTH);

        add(content);
    }
}
