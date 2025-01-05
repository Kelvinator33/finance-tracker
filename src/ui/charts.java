package ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import dbms.db_connection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static dbms.db_connection.*;

public class charts {
    // Datasets for pie and line charts
    private DefaultPieDataset pieDataset;
    private DefaultCategoryDataset lineDataset;

    // Panels to display the charts
    private JPanel pieChartPanel;
    private JPanel lineChartPanel;

    // Getter methods for chart panels and datasets
    public JPanel getLineChartPanel() {
        return lineChartPanel;
    }

    public JPanel getPieChartPanel() {
        return pieChartPanel;
    }

    public DefaultCategoryDataset getLineDataset() {
        return lineDataset;
    }

    public DefaultPieDataset getPieDataset() {
        return pieDataset;
    }
    // Reference to the table object
    table tb;

    // Constructor initializes the table reference
    public charts(table tb) {
        this.tb = tb;
    }



    public JPanel charts() {

        JPanel chartPanel = new JPanel();
        chartPanel.setLayout(null);
        chartPanel.setBounds(10, 140, 400, 600);
        chartPanel.setOpaque(false);

        // Pie Chart
        pieDataset = new DefaultPieDataset();
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Expense Distribution",
                pieDataset,
                true,
                true,
                false
        );
        PiePlot piePlot = (PiePlot) pieChart.getPlot();
        pieChart.setBackgroundPaint(null);
        piePlot.setBackgroundPaint(null);
        piePlot.setOutlineVisible(false);

        pieChartPanel = new ChartPanel(pieChart);
        pieChartPanel.setBounds(0, 0, 400, 250);
        pieChartPanel.setOpaque(false);
        pieChartPanel.setBackground(new Color(0, 0, 0, 0));
        pieChartPanel.setBorder(BorderFactory.createEmptyBorder());
        chartPanel.add(pieChartPanel);

        // Line Chart
        lineDataset = new DefaultCategoryDataset();
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Monthly Expenditure",
                "Month",
                "Amount",
                lineDataset
        );
        CategoryPlot linePlot = lineChart.getCategoryPlot();
        lineChart.setBackgroundPaint(null);
        linePlot.setBackgroundPaint(null);
        lineChartPanel = new ChartPanel(lineChart);
        lineChartPanel.setOpaque(false);
        lineChartPanel.setBackground(new Color(0, 0, 0, 0));
        lineChartPanel.setBounds(0, 260, 400, 250);

        chartPanel.add(lineChartPanel);

        return chartPanel;
    }

    public void updateCharts() {
        // Clear existing data
        pieDataset.clear();
        lineDataset.clear();

        // SQL query to fetch transaction data
        String sql = "SELECT type, description, date, amount FROM transactions";

        try (Connection db_connection = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = db_connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Iterate over the result set
            while (rs.next()) {
                String type = rs.getString("type");
                String description = rs.getString("description");
                String date = rs.getString("date");
                double amount = rs.getDouble("amount");

                // Update pie chart data for expenses
                if (type.equalsIgnoreCase("Expense")) {
                    try {
                        double existingAmount = pieDataset.getValue(description).doubleValue();
                        pieDataset.setValue(description, existingAmount + amount);
                    } catch (org.jfree.data.UnknownKeyException e) {
                        // If the key does not exist, add it
                        pieDataset.setValue(description, amount);
                    }
                }


                // Update line chart data for monthly expenses
                if (type.equalsIgnoreCase("Expense")) {
                    String month = date.substring(0, 7); // Extract year-month
                    lineDataset.addValue(amount, "Expense", month);
                }
            }
        } catch (Exception e) {
            // Handle exceptions and display an error message
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating charts from database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Refresh the chart panels
        pieChartPanel.revalidate();
        pieChartPanel.repaint();
        lineChartPanel.revalidate();
        lineChartPanel.repaint();
    }

}
