package ui;

import expense_tracker.ExpenseTracker;

import javax.swing.*;
import java.awt.*;

public class status {
    private JPanel status;
    ExpenseTracker tracker;
    public status(ExpenseTracker tracker) {
        this.tracker = tracker;
    }








    public JPanel status(){
        // Main status panel
        status = new JPanel();
        status.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10)); // Tiles aligned left with some space
        status.setOpaque(false);
        status.setBounds(10, 10, tracker.getFrame().getWidth() - 20, 60); // Set bounds for the tiles panel

        //expense tile
        JPanel expenseTile = new JPanel();
        expenseTile.setBackground(new Color(255, 85, 85, 100));
        expenseTile.setPreferredSize(new Dimension(200, 60));
        expenseTile.setLayout(new BoxLayout(expenseTile, BoxLayout.Y_AXIS));
        expenseTile.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 2));


        JLabel expenseLabel = new JLabel("Expense", JLabel.CENTER);
        expenseLabel.setForeground(Color.WHITE);
        expenseLabel.setFont(new Font("Arial", Font.BOLD, 14));

        tracker.setExpenseValue(new JLabel("€" + tracker.getTotalExpense(), JLabel.CENTER));
        tracker.getExpenseValue().setForeground(Color.WHITE);
        tracker.getExpenseValue().setFont(new Font("Arial", Font.BOLD, 16));
        tracker.getExpenseValue().setAlignmentX(Component.CENTER_ALIGNMENT);

        expenseTile.add(expenseLabel);
        expenseTile.add(tracker.getExpenseValue());

        //income tile
        JPanel incomeTile = new JPanel();
        incomeTile.setBackground(new Color(85, 255, 85,100)); // Green for income
        incomeTile.setPreferredSize(new Dimension(200, 60));
        incomeTile.setLayout(new BoxLayout(incomeTile, BoxLayout.Y_AXIS));
        incomeTile.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 2));

        JLabel incomeLabel = new JLabel("Income", JLabel.CENTER);
        incomeLabel.setForeground(Color.WHITE);
        incomeLabel.setFont(new Font("Arial", Font.BOLD, 14));

        tracker.setIncomeValue(new JLabel("€" + tracker.getTotalIncome(), JLabel.CENTER));
        tracker.getIncomeValue().setForeground(Color.WHITE);
        tracker.getIncomeValue().setFont(new Font("Arial", Font.BOLD, 16));
        tracker.getIncomeValue().setAlignmentX(Component.CENTER_ALIGNMENT);

        incomeTile.add(incomeLabel);
        incomeTile.add(tracker.getIncomeValue());

        //total tile
        JPanel totalTile = new JPanel();
        totalTile.setBackground(new Color(85, 85, 255,100)); // Blue for total
        totalTile.setPreferredSize(new Dimension(200, 60));
        totalTile.setLayout(new BoxLayout(totalTile, BoxLayout.Y_AXIS));
        totalTile.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 2));


        JLabel totalLabelTitle = new JLabel("Total", JLabel.CENTER);
        totalLabelTitle.setForeground(Color.WHITE);
        totalLabelTitle.setFont(new Font("Arial", Font.BOLD, 14));

        tracker.setTotalLabel(new JLabel("€" + (tracker.getTotalBalance()), JLabel.CENTER));
        tracker.getTotalLabel().setForeground(Color.WHITE);
        tracker.getTotalLabel().setFont(new Font("Arial", Font.BOLD, 16));
        tracker.getTotalLabel().setAlignmentX(Component.CENTER_ALIGNMENT);

        totalTile.add(totalLabelTitle);
        totalTile.add(tracker.getTotalLabel());

        // Add all tiles to the tiles panel
        status.add(expenseTile);
        status.add(incomeTile);
        status.add(totalTile);
        return status;
    }

    //Updates the status panel with the latest expense, income, and total values.
    public void updateStatus() {
        tracker.getExpenseValue().setText("€" + tracker.getTotalExpense());
        tracker.getIncomeValue().setText("€" + tracker.getTotalIncome());
        tracker.getTotalLabel().setText("€" + (tracker.getTotalBalance()));

        tracker.getExpenseValue().revalidate();
        tracker.getIncomeValue().revalidate();
        tracker.getTotalLabel().revalidate();

        status.revalidate();
        status.repaint();
    }

}
