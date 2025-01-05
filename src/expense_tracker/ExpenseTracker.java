package expense_tracker;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import dbms.db_connection;
import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import  org.jfree.data.general.DefaultPieDataset;
import ui.*;

import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ExpenseTracker {
    private JFrame frame;

    // Panels and Buttons for UI components
    private JPanel dashboard, buttons;
    private JButton addExpense, removeExpenses;


    private double totalIncome = 0.0; // Total income tracker
    private double totalExpense = 0.0; // Total expense tracker

    // Labels for displaying income, expenses, and total balance
    private JLabel incomeValue;
    private JLabel expenseValue;
    private JLabel totalLabel;
    private boolean dummyDataVisible = false;
    titlebar title;
    status status_p;
    table expense_table;
    add_and_remove ar;
    charts ch;

    // Drag functionality for custom title bar
    private boolean is_drag = false;
    private Point mouseOffset;

    // Setters and Getters for various components
    public void setDummyDataVisible(boolean dummyDataVisible) {
        this.dummyDataVisible = dummyDataVisible;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public void setIncomeValue(JLabel incomeValue) {
        this.incomeValue = incomeValue;
    }

    public void setExpenseValue(JLabel expenseValue) {
        this.expenseValue = expenseValue;
    }

    public void setTotalLabel(JLabel totalLabel) {
        this.totalLabel = totalLabel;
    }

    public void setIs_drag(boolean is_drag) {
        this.is_drag = is_drag;
    }

    public void setMouseOffset(Point mouseOffset) {
        this.mouseOffset = mouseOffset;
    }

    public boolean isDummyDataVisible() {
        return dummyDataVisible;
    }

    public JFrame getFrame() {
        return frame;
    }

    public JLabel getIncomeValue() {
        return incomeValue;
    }

    public JLabel getExpenseValue() {
        return expenseValue;
    }

    public JLabel getTotalLabel() {
        return totalLabel;
    }

    public boolean isIs_drag() {
        return is_drag;
    }

    public Point getMouseOffset() {
        return mouseOffset;
    }



     // Retrieves the total expense from the database by summing up all 'expense' type transactions.

    public double getTotalExpense() {
        String query = "SELECT SUM(amount) FROM transactions WHERE LOWER(type) = 'expense'";
        try (Connection conn = db_connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                totalExpense = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalExpense;
    }

    // Retrieves the total income from the database by summing up all 'income' type transactions.

    public double getTotalIncome() {
        String query = "SELECT SUM(amount) FROM transactions WHERE LOWER(type) = 'income'";
        try (Connection conn = db_connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                totalIncome = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalIncome;
    }

    // Calculates the total balance as the difference between total income and total expenses.

    public double getTotalBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    /*
    Constructor for the ExpenseTracker application.
    Initializes the frame, components, and functionality.
    */
    public ExpenseTracker() {
        frame = new JFrame("Expense Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(52,73,94)));

        //initialize components
        title=new titlebar(this);
        title.TitleBar();
        status_p=new status(this);
        expense_table=new table(this);
        ch=new charts(expense_table);

        ar=new add_and_remove(this,status_p,expense_table,ch);

        // Build dashboard UI and charts
        Dashboard();
        ch.updateCharts();
        ar.addDummyData();



        frame.setVisible(true);
        System.out.println(dummyDataVisible);

        // Show a welcome message if dummy data is visible
        if(dummyDataVisible) {
            JOptionPane.showMessageDialog(frame, "Click ok to begin.", "Welcome", JOptionPane.INFORMATION_MESSAGE);

        }

        // Clear dummy data after initialization
        ar.clearDummyData();

    }

    // Builds the dashboard panel with its components.
    private void Dashboard() {
        dashboard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient from blue to purple
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(85, 110, 200, 200),
                        getWidth(), getHeight(), new Color(200, 85, 170, 200));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };


        dashboard.setLayout(null);
        dashboard.setBounds(0, 40, frame.getWidth(), frame.getHeight() );
        dashboard.add(buttons(),BorderLayout.NORTH);
        dashboard.add(expense_table.trans_table(),BorderLayout.WEST);
        dashboard.add(status_p.status(),BorderLayout.NORTH);
        dashboard.add(ch.charts(), BorderLayout.EAST);



        frame.add(dashboard);

    }


    // Builds the buttons panel for adding and removing expenses.
    private JPanel buttons(){
        // Buttons panel
        buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.setBounds(10, 10, frame.getWidth() - 20, 50);
        buttons.setOpaque(false);

        // Add Expense button
        addExpense = new JButton("Add");
        addExpense.setPreferredSize(new Dimension(150, 30));
        addExpense.setContentAreaFilled(false); // Makes the button transparent
        addExpense.setOpaque(false); // Ensures transparency
        addExpense.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 2)); // Adds a visible border
        addExpense.setForeground(new Color(52, 73, 94)); // Sets text color
        addExpense.setFont(new Font("Arial", Font.BOLD, 14)); // Optional: Set a custom font
        addExpense.addActionListener(e -> ar.addExpense());

        // Remove Expense button
        removeExpenses = new JButton("Remove");
        removeExpenses.setPreferredSize(new Dimension(150, 30));
        removeExpenses.setContentAreaFilled(false); // Makes the button transparent
        removeExpenses.setOpaque(false); // Ensures transparency
        removeExpenses.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 2)); // Adds a visible border
        removeExpenses.setForeground(new Color(52, 73, 94)); // Sets text color
        removeExpenses.setFont(new Font("Arial", Font.BOLD, 14)); // Optional: Set a custom font
        removeExpenses.addActionListener(e -> ar.removeSelectedExpenses());

        // Add buttons to panel
        buttons.add(addExpense);
        buttons.add(removeExpenses);
        return buttons;

    }



}
