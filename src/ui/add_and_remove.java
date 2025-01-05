package ui;

import dbms.db_connection;
import expense_tracker.ExpenseTracker;
import dbms.db_connection.*;

import java.sql.*;


import javax.swing.*;

import static dbms.db_connection.*;

public class add_and_remove {
    // Dependencies injected from other classes
    ExpenseTracker tracker;
    status sp;
    table tb;
    charts ch;

    public add_and_remove(ExpenseTracker tracker, status status, table tb, charts ch) {
        this.tracker = tracker;
        this.sp = status;
        this.tb = tb;
        this.ch = ch;
    }

    //Adds a new expense or income to the database and updates UI components.
    public void addExpense() {
        // Get the type of transaction (Expense/Income)

        String type = null;
        while (true) {
            type = JOptionPane.showInputDialog(tracker.getFrame(), "Enter the type (Expense/Income):");

            if (type == null) { // User canceled the input
                JOptionPane.showMessageDialog(tracker.getFrame(), "Type input canceled.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (type.equalsIgnoreCase("Expense") || type.equalsIgnoreCase("Income")) {
                type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase(); // Normalize the case
                break;
            } else {
                JOptionPane.showMessageDialog(tracker.getFrame(), "Invalid type! Please enter 'Expense' or 'Income'.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        // Get the date in YYYY-MM-DD format
        String date = null;
        while (true) {
            date = JOptionPane.showInputDialog(tracker.getFrame(), "Enter the date (YYYY-MM-DD):");

            if (date == null) { // User canceled the input
                JOptionPane.showMessageDialog(tracker.getFrame(), "Date input canceled.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (date.matches("\\d{4}-\\d{2}-\\d{2}")) { // Simple regex validation for date format
                break;
            } else {
                JOptionPane.showMessageDialog(tracker.getFrame(), "Invalid date format! Please enter the date as YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        //get amount
        double amount = 0;
        while (true) {
            String amountStr = JOptionPane.showInputDialog(tracker.getFrame(), "Enter the amount:");

            if (amountStr == null) { // User canceled the input
                JOptionPane.showMessageDialog(tracker.getFrame(), "Amount input canceled.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                amount = Double.parseDouble(amountStr);
                if (amount > 0) {
                    break;
                } else {
                    JOptionPane.showMessageDialog(tracker.getFrame(), "Amount must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(tracker.getFrame(), "Invalid amount! Please enter a valid numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Get the description from a predefined list
        String[] validDescriptions = type.equalsIgnoreCase("Income")
                ? new String[]{"Salary"}
                : new String[]{"Restaurant", "Medicine", "Shopping", "Grocery", "Stocks", "Savings", "Fuel"};
        String description = null;
        while (true) {
            description = (String) JOptionPane.showInputDialog(
                    tracker.getFrame(),
                    "Select a description:",
                    "Description",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    validDescriptions,
                    validDescriptions[0] // Default option
            );
            if (description == null) { // User canceled the input
                JOptionPane.showMessageDialog(tracker.getFrame(), "Description input canceled.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (String validDescription : validDescriptions) {
                if (description.equalsIgnoreCase(validDescription)) {
                    description = validDescription; // Normalize the case
                    break;
                }
            }

            if (description != null) {
                break;
            } else {
                JOptionPane.showMessageDialog(tracker.getFrame(), "Invalid description! Please choose one of the following:\n"
                        + String.join(", ", validDescriptions), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Insert data into the database
        try (Connection db_connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            // Insert into the database
            String sql = "INSERT INTO transactions (type, date, amount, description) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = db_connection.prepareStatement(sql);
            stmt.setString(1, type);
            stmt.setString(2, date);
            stmt.setDouble(3, amount);
            stmt.setString(4, description);
            stmt.executeUpdate();

            // Update totals based on type
            if (type.equalsIgnoreCase("Expense")) {
                tracker.setTotalExpense(tracker.getTotalExpense() + amount);
            } else if(type.equalsIgnoreCase("Income")) {
                tracker.setTotalIncome(tracker.getTotalIncome() + amount);
            }

            sp.updateStatus();
            tb.refreshTable(); // Reload data from the database
            ch.updateCharts();

            JOptionPane.showMessageDialog(tracker.getFrame(), type + " added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(tracker.getFrame(), "Error saving to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    //Removes selected transactions from the database and updates the UI
    public void removeSelectedExpenses() {
        int[] selectedRows = tb.getTransactions().getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(tracker.getFrame(), "No rows selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (Connection db_connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            String sql = "DELETE FROM transactions WHERE id = ?";
            PreparedStatement stmt = db_connection.prepareStatement(sql);

            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int id = (int) tb.getTableModel().getValueAt(selectedRows[i], 0); // Get the ID
                stmt.setInt(1, id);
                stmt.executeUpdate();

                String type = tb.getTableModel().getValueAt(selectedRows[i], 1).toString();
                double amount = Double.parseDouble(tb.getTableModel().getValueAt(selectedRows[i], 4).toString());

                // Update totals
                if (type.equalsIgnoreCase("Expense")) {
                    tracker.setTotalExpense(tracker.getTotalExpense() - amount);
                } else {
                    tracker.setTotalIncome(tracker.getTotalIncome() - amount);
                }

                tb.getTableModel().removeRow(selectedRows[i]);
            }

            sp.updateStatus();
            ch.updateCharts();

            JOptionPane.showMessageDialog(tracker.getFrame(), "Selected expenses removed successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(tracker.getFrame(), "Error deleting from database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }


    //Adds dummy data to the database for testing purposes.
    public void addDummyData() {

        try (Connection db_connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Connection conn = dbms.db_connection.getConnection();){
            // Check if the table is empty
            String checkSql = "SELECT COUNT(*) FROM transactions";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            ResultSet rs = checkStmt.executeQuery();

            int rowCount = 0;
            if (rs.next()) {
                rowCount = rs.getInt(1); // Get the count of rows
            }

            if (rowCount > 0) {
                // If there are rows, skip adding dummy data
                System.out.println("Skipping dummy data insertion: Table already contains data.");
                return;
            }


            String sql = "INSERT INTO transactions (type, date, amount, description,is_dummy) VALUES (?, ?, ?, ?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);


            Object[][] dummyData = {
                    {1, "Expense", "2023-01-01", "Food", 200.0},
                    {2, "Expense", "2023-01-15", "Rent", 800.0},
                    {3, "Income", "2023-01-20", "Salary", 2000.0},
                    {4, "Expense", "2023-02-01", "Fuel", 100.0},
                    {5, "Expense", "2023-02-15", "Stocks", 500.0}
            };



            for (Object[] row : dummyData) {
                stmt.setString(1, (String) row[1]);
                stmt.setString(2, (String) row[2]);
                stmt.setDouble(3, (Double) row[4]);
                stmt.setString(4, (String) row[3]);
                stmt.setBoolean(5, true);
                stmt.executeUpdate();
            }

            tb.refreshTable();
            sp.updateStatus();
            ch.updateCharts();
            tracker.setDummyDataVisible(true);


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(tracker.getFrame(), "Error adding dummy data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }


    }

    //Clears all dummy data from the database
    public void clearDummyData() {

        try (Connection db_connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            String sql = "DELETE FROM transactions WHERE is_dummy=1";
            PreparedStatement stmt = db_connection.prepareStatement(sql);
            stmt.executeUpdate();

            String sql1="alter table transactions auto_increment = 1";
            PreparedStatement stmt1 = db_connection.prepareStatement(sql1);
            stmt1.executeUpdate();

            tracker.setTotalIncome(0.0);
            tracker.setTotalExpense(0.0);

            tb.refreshTable();
            sp.updateStatus();
            ch.updateCharts();



        } catch (SQLException e) {
            JOptionPane.showMessageDialog(tracker.getFrame(), "Error clearing dummy data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    }

