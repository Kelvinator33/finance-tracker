package ui;

import expense_tracker.ExpenseTracker;

import javax.swing.*;
import java.awt.*;


public class titlebar {
    // Components of the title bar
    private JPanel titleBar;
    private JLabel title, close, minimize;
    ExpenseTracker expenseTracker;


    //Constructor for titlebar with an ExpenseTracker instance.
    public titlebar(ExpenseTracker tracker) {
        this.expenseTracker = tracker; // Use the existing ExpenseTracker instance
    }

    //titlebar configuration
    public void TitleBar() {
        //title bar panel
        titleBar = new JPanel();
        titleBar.setLayout(null);
        titleBar.setBackground(new Color(60, 63, 65));
        titleBar.setBounds(0, 0, expenseTracker.getFrame().getWidth(), 40);

        // Title label setup
        title = new JLabel("Expense Tracker");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 10, 200, 20);

        // Close button setup
        close = new JLabel("X");
        close.setForeground(Color.WHITE);
        close.setFont(new Font("Arial", Font.BOLD, 14));
        close.setBounds(expenseTracker.getFrame().getWidth() - 30, 10, 20, 20);
        close.setHorizontalAlignment(SwingConstants.CENTER);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.exit(0); // Exit application on click
            }
        });

        // Minimize button setup
        minimize = new JLabel("_");
        minimize.setForeground(Color.WHITE);
        minimize.setFont(new Font("Arial", Font.BOLD, 14));
        minimize.setBounds(expenseTracker.getFrame().getWidth() - 60, 10, 20, 20);
        minimize.setHorizontalAlignment(SwingConstants.CENTER);
        minimize.setCursor(new Cursor(Cursor.HAND_CURSOR));
        minimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                expenseTracker.getFrame().setState(JFrame.ICONIFIED); // Minimize application
            }
        });


        // Drag listener for frame movement
        titleBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                expenseTracker.setMouseOffset(evt.getPoint()) ;
                expenseTracker.setIs_drag(true);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                expenseTracker.setIs_drag(false);
            }
        });

        // Detects mouse movement to drag the frame
        titleBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                if (expenseTracker.isIs_drag()) {
                    Point current = evt.getLocationOnScreen();
                    expenseTracker.getFrame().setLocation(current.x - expenseTracker.getMouseOffset().x, current.y - expenseTracker.getMouseOffset().y);
                }
            }
        });

        // Add components to the title bar panel
        titleBar.add(title);
        titleBar.add(close);
        titleBar.add(minimize);
        expenseTracker.getFrame().add(titleBar);
    }

    // Getter methods for accessing the title bar components
    public JPanel getTitleBar() {
        return titleBar;
    }

    public JLabel getTitle() {
        return title;
    }

    public JLabel getClose() {
        return close;
    }

    public JLabel getMinimize() {
        return minimize;
    }
}
