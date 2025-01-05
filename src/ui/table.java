package ui;

import dbms.db_connection;
import expense_tracker.ExpenseTracker;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class table {
    ExpenseTracker tracker;
    public table(ExpenseTracker tracker) {
        this.tracker = tracker;
    }

    private DefaultTableModel tableModel;
    private JTable transactions;

    public JTable getTransactions() {
        return transactions;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void setTransactions(JTable transactions) {
        this.transactions = transactions;
    }

    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }


    //Creates a populated transactions table panel by fetching data from the database.
    public JPanel trans_table() {
        // Define the column headers for the table.
        String[] columnNames = {"ID", "Type", "Date", "Description", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0);


        // fetch all transaction data.
        String query = "SELECT * FROM transactions";
        try (Connection conn = db_connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {


            // Populate the table with data
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getDate("date"),
                        rs.getString("description"),
                        rs.getDouble("amount")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Customize the table appearance and properties
        transactions = new JTable(tableModel);
        transactions.setRowHeight(30);
        transactions.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transactions.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        transactions.setOpaque(false);
        transactions.getTableHeader().setBackground(new Color(60, 63, 65));
        transactions.getTableHeader().setForeground(Color.WHITE);
        transactions.setShowGrid(true);
        transactions.setGridColor(new Color(255,255,255,255));
        ((DefaultTableCellRenderer) transactions.getDefaultRenderer(Object.class)).setOpaque(false);
        transactions.setIntercellSpacing(new Dimension(1, 1));

        // Alternate row colors
        transactions.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(200, 200, 255,150));

                } else{
                    c.setBackground(row % 2 == 0 ? new Color(255, 255, 255, 150) : new Color(245, 245, 245, 150));
                }
                return c;
            }
        });


        //panel to hold the table and set its properties.
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Align the table to the right
        tablePanel.setOpaque(false);
        tablePanel.setBounds(10, 70, tracker.getFrame().getWidth() - 20, tracker.getFrame().getHeight() - 120); // Set bounds for the table panel
        tablePanel.add(tableScrollPane());

        return tablePanel;
    }

    //Refreshes the data in the transactions table by re-fetching it from the database.
    public void refreshTable() {
        // Clears the current table data
        tableModel.setRowCount(0);

        // Fetches data from the database and populate the table
        try (Connection conn = db_connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM transactions")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                String date = rs.getString("date");
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");

                tableModel.addRow(new Object[]{id, type, date, description, amount});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error refreshing table: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    //Creates and customizes a JScrollPane to hold the JTable.
    private JScrollPane tableScrollPane(){
        JScrollPane sp = new JScrollPane(transactions);
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);

        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setBackground(new Color(211, 211, 211, 80));
        sp.getViewport().setBackground(new Color(211, 211, 211, 80));


        //customize vertical scrollbar appearance
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI(){
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(169, 169, 169, 150); // Darker grey, semi-transparent
                this.trackColor = new Color(211, 211, 211, 50); // Very light grey, very transparent
            }

        });
        //Customize horizontal scrollbar appearance.
        sp.getHorizontalScrollBar().setUI(new BasicScrollBarUI(){

            protected void configureScrollBarColors() {
                this.thumbColor = new Color(169, 169, 169, 150); // Darker grey, semi-transparent
                this.trackColor = new Color(211, 211, 211, 50); // Very light grey, very transparent
            }

        });
        return sp;

    }
}
