package com.habittracker;

/**
 * HabitTrackerApp.java
 * Main application class with GUI implementation using Java Swing.
 */
import java.awt.*;
import java.util.List;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class HabitTrackerApp extends JFrame {
    private static final long serialVersionUID = 1L;
    private List<Habit> habits;
    private final DefaultTableModel tableModel;
    private final JTable habitTable;
    private final JLabel statusLabel;

    /**
     * Constructor for HabitTrackerApp.
     * Initializes the GUI and loads existing habits.
     */
    public HabitTrackerApp() {
        // Initialize habits list
        habits = DataManager.loadHabits();

        // Set up the main window
        setTitle("Habit Tracker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add window listener to save data on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                DataManager.saveHabits(habits);
            }
        });

        // Create main panel with BorderLayout and gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(240, 248, 255); // Alice Blue
                Color color2 = new Color(230, 240, 250); // Slightly darker blue
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setOpaque(false);

        // Create title label with improved shadow effect and better styling
        JLabel titleLabel = new JLabel("My Habit Tracker", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Create a more subtle shadow with blur effect
                Font font = getFont();
                FontMetrics fm = g2d.getFontMetrics(font);
                int textWidth = fm.stringWidth(getText());
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;

                // Draw multiple shadow layers for a soft effect
                g2d.setColor(new Color(150, 150, 150, 80)); // Lighter, semi-transparent shadow
                for (int i = 3; i > 0; i--) {
                    g2d.drawString(getText(), x + i, y + i);
                }

                // Draw the main text
                g2d.setColor(getForeground());
                g2d.drawString(getText(), x, y);
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 102, 204)); // Nice blue color
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 10, 20, 10),
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 130, 180, 100))));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(240, 248, 255, 200)); // Semi-transparent background
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create table for habits with better styling
        String[] columnNames = {"Name", "Category", "Completed Today", "Streak"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) { // Completed Today column
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        habitTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!c.getBackground().equals(getSelectionBackground())) {
                    // Alternating row colors
                    c.setBackground(row % 2 == 0 ? new Color(255, 255, 255) : new Color(245, 250, 255));
                }
                return c;
            }
        };
        habitTable.setRowHeight(30);
        habitTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        habitTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        habitTable.getTableHeader().setBackground(new Color(70, 130, 180)); // Steel Blue
        habitTable.getTableHeader().setForeground(Color.WHITE);
        habitTable.setShowGrid(true);
        habitTable.setGridColor(new Color(220, 220, 220));
        habitTable.setSelectionBackground(new Color(173, 216, 230)); // Light Blue
        habitTable.setSelectionForeground(Color.BLACK);
        habitTable.setIntercellSpacing(new Dimension(5, 5));
        habitTable.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(habitTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel with better styling
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255)); // Alice Blue background
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Define button colors and styles
        Color buttonColor = new Color(70, 130, 180); // Steel Blue
        Color buttonHoverColor = new Color(50, 110, 160); // Darker blue for hover
        Color buttonTextColor = Color.WHITE;
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        // Create a method to style buttons consistently
        Function<JButton, JButton> styleButton = button -> {
            button.setBackground(buttonColor);
            button.setForeground(buttonTextColor);
            button.setFont(buttonFont);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 100, 150), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
            button.setContentAreaFilled(false);
            button.setOpaque(true);

            // Add hover effect
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(buttonHoverColor);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(buttonColor);
                }
            });

            return button;
        };

        // Create icons using Unicode characters
        String addIcon = "➕ ";
        String completeIcon = "✅ ";
        String deleteIcon = "🗑️ ";
        String saveIcon = "💾 ";

        // Add Habit button
        JButton addButton = styleButton.apply(new JButton(addIcon + "Add Habit"));
        addButton.addActionListener(_ -> addHabit());
        buttonPanel.add(addButton);

        // Mark Complete button
        JButton completeButton = styleButton.apply(new JButton(completeIcon + "Mark Complete"));
        completeButton.addActionListener(_ -> markComplete());
        buttonPanel.add(completeButton);

        // Delete Habit button
        JButton deleteButton = styleButton.apply(new JButton(deleteIcon + "Delete Habit"));
        deleteButton.addActionListener(_ -> deleteHabit());
        buttonPanel.add(deleteButton);

        // Save button
        JButton saveButton = styleButton.apply(new JButton(saveIcon + "Save"));
        saveButton.addActionListener(_ -> saveHabits());
        buttonPanel.add(saveButton);

        // Status label with better styling and animation
        statusLabel = new JLabel() {
            private float alpha = 1.0f;
            private boolean fadeIn = true;
            private Timer fadeTimer;

            @Override
            public void addNotify() {
                super.addNotify();
                // Initialize the timer after the component is added to a container
                fadeTimer = new Timer(30, _ -> {
                    if (fadeIn) {
                        alpha += 0.05f;
                        if (alpha >= 1.0f) {
                            alpha = 1.0f;
                            fadeIn = false;
                            fadeTimer.stop();
                        }
                    } else {
                        alpha -= 0.05f;
                        if (alpha <= 0.7f) {
                            alpha = 0.7f;
                            fadeIn = true;
                        }
                    }
                    repaint();
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }

            @Override
            public void setText(String text) {
                super.setText(text);
                if (fadeTimer != null) {
                    fadeTimer.start();
                }
            }
        };
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(new Color(0, 102, 204)); // Nice blue color
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204, 100), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(240, 248, 255, 200)); // Semi-transparent background
        updateStatusLabel();
        buttonPanel.add(Box.createHorizontalStrut(20)); // Add some space
        buttonPanel.add(statusLabel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to the frame
        add(mainPanel);

        // Populate the table with existing habits
        refreshTable();
    }

    /**
     * Updates the status label with total habits and completed count.
     */
    private void updateStatusLabel() {
        int total = habits.size();
        int completed = 0;
        for (Habit habit : habits) {
            if (habit.isCompletedToday()) {
                completed++;
            }
        }
        statusLabel.setText("Total Habits: " + total + " | Completed Today: " + completed);
    }

    /**
     * Refreshes the table with current habits data.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Habit habit : habits) {
            Object[] row = {
                habit.getName(),
                habit.getCategory(),
                habit.isCompletedToday(),
                habit.getStreak()
            };
            tableModel.addRow(row);
        }
        updateStatusLabel();
    }

    /**
     * Opens a dialog to add a new habit.
     */
    private void addHabit() {
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();

        // Better styling for input fields
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        categoryField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Create a better looking panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components with better spacing
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Habit Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(categoryField, gbc);

        // Create a custom dialog with better styling
        JDialog dialog = new JDialog(this, "Add New Habit", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);

        // Create button panel
        JPanel dialogButtonPanel = new JPanel(new FlowLayout());
        dialogButtonPanel.setBackground(new Color(240, 248, 255));

        JButton okButton = new JButton("Add");
        okButton.setBackground(new Color(70, 130, 180));
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(70, 130, 180));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        dialogButtonPanel.add(okButton);
        dialogButtonPanel.add(cancelButton);
        dialog.add(dialogButtonPanel, BorderLayout.SOUTH);

        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        // Add action listeners
        okButton.addActionListener(_ -> {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();

            if (!name.isEmpty() && !category.isEmpty()) {
                Habit newHabit = new Habit(name, category, false, 0);
                habits.add(newHabit);
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(_ -> dialog.dispose());

        dialog.setVisible(true);
    }

    /**
     * Marks the selected habit as complete and updates the streak.
     */
    private void markComplete() {
        int selectedRow = habitTable.getSelectedRow();
        if (selectedRow >= 0) {
            Habit habit = habits.get(selectedRow);
            if (!habit.isCompletedToday()) {
                habit.setCompletedToday(true);
                habit.setStreak(habit.getStreak() + 1);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "This habit is already marked as complete today.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a habit to mark as complete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected habit.
     */
    private void deleteHabit() {
        int selectedRow = habitTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(
                this, "Are you sure you want to delete this habit?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                habits.remove(selectedRow);
                refreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a habit to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Saves all habits to the file.
     */
    private void saveHabits() {
        DataManager.saveHabits(habits);
        JOptionPane.showMessageDialog(this, "Habits saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Main method to run the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HabitTrackerApp().setVisible(true));
    }
}