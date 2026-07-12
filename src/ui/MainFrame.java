package src.ui;

import java.awt.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import src.model.Transaction;
import src.service.TransactionService;
import src.ui.components.RoundedButton;
import src.utils.DateUtils;

public class MainFrame extends JFrame {

    private final TransactionService service = new TransactionService();
    private DefaultTableModel tableModel;
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##,##0.00"); // Supports Bangladeshi style commas

    // Stats Labels
    private JLabel balanceLabel, todayIncLabel, todayExpLabel;
    private JLabel monthBalLabel, monthIncLabel, monthExpLabel;
    private JLabel prevBalLabel, prevIncLabel, prevExpLabel;
    private JLabel yearSaveLabel, yearIncLabel, yearExpLabel;

    public MainFrame() {
        setTitle("MoneyMap v2.0 - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Best balance: Maximized window
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel contentPane = new JPanel(new BorderLayout(15, 15));
        contentPane.setBackground(new Color(245, 247, 250));
        contentPane.setBorder(new EmptyBorder(25, 25, 25, 25)); // Slightly increased padding

        contentPane.add(createHeaderPanel(), BorderLayout.NORTH);
        contentPane.add(createStatsPanel(), BorderLayout.NORTH);
        contentPane.add(createMainContentPanel(), BorderLayout.CENTER);

        setContentPane(contentPane);
        refreshData();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("MoneyMap");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(33, 37, 41));

        JLabel subtitle = new JLabel("Personal Finance Tracker");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(108, 117, 125));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        leftPanel.setOpaque(false);
        leftPanel.add(title);
        leftPanel.add(subtitle);

        header.add(leftPanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 0, 25, 0));

        statsPanel.add(createModernCard("Current Balance", balanceLabel = createValueLabel(new Color(0, 102, 204)),
                todayIncLabel = createSmallLabel(), todayExpLabel = createSmallLabel()));

        statsPanel.add(createModernCard("This Month", monthBalLabel = createValueLabel(new Color(0, 102, 204)),
                monthIncLabel = createSmallLabel(), monthExpLabel = createSmallLabel()));

        statsPanel.add(createModernCard("Previous Month", prevBalLabel = createValueLabel(new Color(0, 102, 204)),
                prevIncLabel = createSmallLabel(), prevExpLabel = createSmallLabel()));

        statsPanel.add(createModernCard("Last 12 Months", yearSaveLabel = createValueLabel(new Color(40, 167, 69)),
                yearIncLabel = createSmallLabel(), yearExpLabel = createSmallLabel()));

        return statsPanel;
    }

    private JLabel createValueLabel(Color color) {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(color);
        return label;
    }

    private JLabel createSmallLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(75, 80, 85));
        return label;
    }

    private JPanel createModernCard(String title, JLabel mainVal, JLabel val1, JLabel val2) {
        JPanel card = new JPanel(new GridLayout(4, 1, 0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
                new EmptyBorder(22, 24, 22, 24)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(new Color(70, 75, 80));

        card.add(titleLabel);
        card.add(mainVal);
        card.add(val1);
        card.add(val2);

        return card;
    }

    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);
        panel.add(createInputPanel(), BorderLayout.NORTH);
        panel.add(createTablePanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 14));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
                        "  Add New Transaction  ",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 15), new Color(50, 55, 60)),
                new EmptyBorder(18, 18, 18, 18)));

        JComboBox<String> typeBox = new JComboBox<>(new String[] { "Income", "Expense" });
        styleComboBox(typeBox);

        JTextField amountField = new JTextField(12);
        JTextField dateField = new JTextField(DateUtils.getToday(), 12);
        JTextField noteField = new JTextField(22);

        styleTextField(amountField);
        styleTextField(dateField);
        styleTextField(noteField);

        RoundedButton addBtn = new RoundedButton("Save Transaction", new Color(40, 167, 69), Color.WHITE);
        addBtn.setPreferredSize(new Dimension(160, 42));

        addBtn.addActionListener(e -> addTransaction(typeBox, amountField, dateField, noteField));

        inputPanel.add(new JLabel("Type:"));
        inputPanel.add(typeBox);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Note:"));
        inputPanel.add(noteField);
        inputPanel.add(addBtn);

        return inputPanel;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 38));
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        combo.setPreferredSize(new Dimension(140, 38));
    }

    private void addTransaction(JComboBox<String> typeBox, JTextField amountField,
            JTextField dateField, JTextField noteField) {
        try {
            String type = typeBox.getSelectedItem().toString();
            double amount = Double.parseDouble(amountField.getText().trim());
            String dateStr = dateField.getText().trim();
            String note = noteField.getText().trim();

            if (amount <= 0)
                throw new NumberFormatException("Amount must be positive.");
            if (!DateUtils.isValidDate(dateStr))
                throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD.");

            service.addTransaction(type, amount, Date.valueOf(dateStr), note);

            amountField.setText("");
            noteField.setText("");
            refreshData();

            JOptionPane.showMessageDialog(this, "✅ Transaction added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number for amount.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 14));
        tablePanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        searchPanel.setOpaque(false);

        JComboBox<String> searchType = new JComboBox<>(new String[] { "All", "Income", "Expense" });
        JTextField searchYear = new JTextField(6);
        JTextField searchMonth = new JTextField(4);
        RoundedButton searchBtn = new RoundedButton("Search", new Color(0, 123, 255), Color.WHITE);

        styleComboBox(searchType);
        searchYear.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchMonth.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchYear.setPreferredSize(new Dimension(80, 38));
        searchMonth.setPreferredSize(new Dimension(70, 38));

        searchPanel.add(new JLabel("Filter Type:"));
        searchPanel.add(searchType);
        searchPanel.add(new JLabel("Year:"));
        searchPanel.add(searchYear);
        searchPanel.add(new JLabel("Month (1-12):"));
        searchPanel.add(searchMonth);
        searchPanel.add(searchBtn);

        searchBtn.addActionListener(e -> loadTableData(
                searchType.getSelectedItem().toString(),
                searchYear.getText().trim(),
                searchMonth.getText().trim()));

        String[] columns = { "ID", "Date", "Type", "Amount", "Note" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(238, 240, 244));
        table.setSelectionBackground(new Color(230, 243, 255));
        table.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(249, 250, 252));
        header.setForeground(new Color(60, 64, 67));
        header.setPreferredSize(new Dimension(0, 42));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 252));
                }
                setBorder(new EmptyBorder(8, 12, 8, 12));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true));

        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void refreshData() {
        loadTableData("All", "", "");
        updateStatistics();
    }

    private void loadTableData(String typeFilter, String year, String month) {
        try {
            tableModel.setRowCount(0);
            List<Transaction> transactions = service.getTransactions(typeFilter, year, month);

            for (Transaction t : transactions) {
                tableModel.addRow(new Object[] {
                        t.getId(),
                        DateUtils.DISPLAY_FORMAT.format(t.getDate()),
                        t.getType(),
                        formatCurrency(t.getAmount()),
                        t.getNote()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            LocalDate today = LocalDate.now();

            double totalInc = service.getSum("Income", null, null);
            double totalExp = service.getSum("Expense", null, null);
            double balance = totalInc - totalExp;

            balanceLabel.setText("৳ " + currencyFormat.format(balance));
            balanceLabel.setForeground(balance >= 0 ? new Color(40, 167, 69) : new Color(220, 53, 69));

            // Today
            double todayInc = service.getSum("Income", today, today);
            double todayExp = service.getSum("Expense", today, today);
            todayIncLabel.setText("Income: ৳ " + currencyFormat.format(todayInc));
            todayExpLabel.setText("Expense: ৳ " + currencyFormat.format(todayExp));

            // This Month
            LocalDate monthStart = today.withDayOfMonth(1);
            LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
            double mInc = service.getSum("Income", monthStart, monthEnd);
            double mExp = service.getSum("Expense", monthStart, monthEnd);
            monthBalLabel.setText("Balance: ৳ " + currencyFormat.format(mInc - mExp));
            monthIncLabel.setText("Income: ৳ " + currencyFormat.format(mInc));
            monthExpLabel.setText("Expense: ৳ " + currencyFormat.format(mExp));

            // Previous Month
            LocalDate pStart = monthStart.minusMonths(1);
            LocalDate pEnd = pStart.withDayOfMonth(pStart.lengthOfMonth());
            double pInc = service.getSum("Income", pStart, pEnd);
            double pExp = service.getSum("Expense", pStart, pEnd);
            prevBalLabel.setText("Balance: ৳ " + currencyFormat.format(pInc - pExp));
            prevIncLabel.setText("Income: ৳ " + currencyFormat.format(pInc));
            prevExpLabel.setText("Expense: ৳ " + currencyFormat.format(pExp));

            // Last 12 Months
            LocalDate yStart = today.minusMonths(12);
            double yInc = service.getSum("Income", yStart, today);
            double yExp = service.getSum("Expense", yStart, today);
            yearSaveLabel.setText("Saved: ৳ " + currencyFormat.format(yInc - yExp));
            yearIncLabel.setText("Income: ৳ " + currencyFormat.format(yInc));
            yearExpLabel.setText("Expense: ৳ " + currencyFormat.format(yExp));

        } catch (Exception e) {
            System.err.println("Statistics update error: " + e.getMessage());
        }
    }

    /** Helper method for consistent currency formatting with commas */
    private String formatCurrency(double amount) {
        return "৳ " + currencyFormat.format(amount);
    }
}