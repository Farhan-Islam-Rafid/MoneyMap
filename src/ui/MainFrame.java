package src.ui;

import java.awt.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import src.model.Transaction;
import src.service.TransactionService;
import src.session.Session;
import src.ui.components.RoundedButton;
import src.utils.DateUtils;

public class MainFrame extends JFrame {

    private final TransactionService service = new TransactionService();
    private final Preferences prefs = Preferences.userNodeForPackage(MainFrame.class);
    private DefaultTableModel tableModel;
    private JTable table;
    private JPanel archiveListPanel;
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
        contentPane.add(createArchivePanel(), BorderLayout.EAST);

        setContentPane(contentPane);
        refreshData();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("MoneyMap - " + Session.username);
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

    // ================= Yearly Savings Archive (side panel) =================

    /**
     * Right-side panel that automatically keeps a permanent record of every
     * completed 12-month (1 year) cycle's balance, so history is never lost
     * even after the "Last 12 Months" card rolls forward.
     */
    private JPanel createArchivePanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(250, 0));
        wrapper.setBorder(new EmptyBorder(10, 18, 0, 0));

        JLabel titleLabel = new JLabel("Yearly Savings Archive");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(new Color(33, 37, 41));

        JLabel subLabel = new JLabel("Auto-saved every 12 months");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(130, 135, 140));

        JPanel headingBox = new JPanel(new GridLayout(2, 1, 0, 2));
        headingBox.setOpaque(false);
        headingBox.add(titleLabel);
        headingBox.add(subLabel);

        archiveListPanel = new JPanel();
        archiveListPanel.setLayout(new BoxLayout(archiveListPanel, BoxLayout.Y_AXIS));
        archiveListPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(archiveListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        wrapper.add(headingBox, BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }

    /**
     * Returns the current 12-month cycle's start date, creating one on first run.
     */
    private LocalDate getCycleStart() {
        String stored = prefs.get("cycleStart", null);
        if (stored == null) {
            LocalDate start = LocalDate.now();
            prefs.put("cycleStart", start.toString());
            return start;
        }
        return LocalDate.parse(stored);
    }

    /**
     * Checks whether the running 12-month cycle has finished. If it has, the
     * period's income/expense/balance is permanently archived and a fresh
     * cycle begins — repeating in case the app was closed for over a year.
     */
    private void checkAndArchiveYear() {
        try {
            LocalDate cycleStart = getCycleStart();
            LocalDate today = LocalDate.now();
            boolean archivedAny = false;

            while (!today.isBefore(cycleStart.plusMonths(12))) {
                LocalDate cycleEnd = cycleStart.plusMonths(12).minusDays(1);
                double inc = service.getSum("Income", cycleStart, cycleEnd);
                double exp = service.getSum("Expense", cycleStart, cycleEnd);
                saveArchivedYear(cycleStart, cycleEnd, inc, exp);
                cycleStart = cycleStart.plusMonths(12);
                archivedAny = true;
            }

            if (archivedAny) {
                prefs.put("cycleStart", cycleStart.toString());
            }
        } catch (Exception e) {
            System.err.println("Yearly archive check error: " + e.getMessage());
        }
    }

    private void saveArchivedYear(LocalDate start, LocalDate end, double inc, double exp) {
        String existing = prefs.get("archivedYears", "");
        String record = start + "," + end + "," + inc + "," + exp;
        String updated = existing.isEmpty() ? record : existing + ";" + record;
        prefs.put("archivedYears", updated);
    }

    private void refreshArchivePanel() {
        archiveListPanel.removeAll();
        String data = prefs.get("archivedYears", "");

        if (data.isEmpty()) {
            JLabel empty = new JLabel(
                    "<html><div style='width:190px; color:#82878C; font-family:Segoe UI;'>"
                            + "No completed 12-month cycle yet. Once a full year passes, its balance "
                            + "will be saved here automatically.</div></html>");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            empty.setBorder(new EmptyBorder(14, 4, 14, 4));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            archiveListPanel.add(empty);
        } else {
            String[] records = data.split(";");
            for (int i = records.length - 1; i >= 0; i--) {
                String[] parts = records[i].split(",");
                LocalDate start = LocalDate.parse(parts[0]);
                LocalDate end = LocalDate.parse(parts[1]);
                double inc = Double.parseDouble(parts[2]);
                double exp = Double.parseDouble(parts[3]);

                JPanel card = createArchiveCard(start, end, inc, exp);
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                archiveListPanel.add(card);
                archiveListPanel.add(Box.createVerticalStrut(12));
            }
        }

        archiveListPanel.revalidate();
        archiveListPanel.repaint();
    }

    private JPanel createArchiveCard(LocalDate start, LocalDate end, double inc, double exp) {
        double balance = inc - exp;

        JPanel card = new JPanel(new GridLayout(3, 1, 0, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(240, 100));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel period = new JLabel(DateUtils.DISPLAY_FORMAT.format(Date.valueOf(start)) + "  →  "
                + DateUtils.DISPLAY_FORMAT.format(Date.valueOf(end)));
        period.setFont(new Font("Segoe UI", Font.BOLD, 12));
        period.setForeground(new Color(100, 105, 110));

        JLabel bal = new JLabel("৳ " + currencyFormat.format(balance));
        bal.setFont(new Font("Segoe UI", Font.BOLD, 19));
        bal.setForeground(balance >= 0 ? new Color(40, 167, 69) : new Color(220, 53, 69));

        JLabel detail = new JLabel("In: ৳" + currencyFormat.format(inc) + "   Out: ৳" + currencyFormat.format(exp));
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detail.setForeground(new Color(120, 125, 130));

        card.add(period);
        card.add(bal);
        card.add(detail);
        return card;
    }

    // ========================================================================

    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);
        panel.add(createInputPanel(), BorderLayout.NORTH);
        panel.add(createTablePanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
                        "  Add New Transaction  ",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 15), new Color(50, 55, 60)),
                new EmptyBorder(18, 18, 18, 18)));

        // Fields row (left/center) — GridBagLayout so it stays on ONE line and
        // the Note field never gets wrapped/clipped, unlike FlowLayout.
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;

        JComboBox<String> typeBox = new JComboBox<>(new String[] { "Income", "Expense" });
        styleComboBox(typeBox);

        JTextField amountField = new JTextField(10);
        JTextField dateField = new JTextField(DateUtils.getToday(), 10);
        JTextField noteField = new JTextField(15);

        styleTextField(amountField);
        styleTextField(dateField);
        styleTextField(noteField);

        int col = 0;
        gbc.gridx = col++;
        fieldsPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = col++;
        fieldsPanel.add(typeBox, gbc);
        gbc.gridx = col++;
        fieldsPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = col++;
        fieldsPanel.add(amountField, gbc);
        gbc.gridx = col++;
        fieldsPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = col++;
        fieldsPanel.add(dateField, gbc);
        gbc.gridx = col++;
        fieldsPanel.add(new JLabel("Note:"), gbc);
        gbc.gridx = col++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(noteField, gbc);

        // Save button, anchored on the right and vertically centered
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);

        RoundedButton addBtn = new RoundedButton("Save Transaction", new Color(40, 167, 69), Color.WHITE);
        addBtn.setPreferredSize(new Dimension(170, 42));
        addBtn.addActionListener(e -> addTransaction(typeBox, amountField, dateField, noteField));

        btnPanel.add(addBtn);

        inputPanel.add(fieldsPanel, BorderLayout.CENTER);
        inputPanel.add(btnPanel, BorderLayout.EAST);

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

        // Top row: filters on the left, Edit/Delete actions on the right
        JPanel searchRow = new JPanel(new BorderLayout());
        searchRow.setOpaque(false);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        filterPanel.setOpaque(false);

        JComboBox<String> searchType = new JComboBox<>(new String[] { "All", "Income", "Expense" });
        JTextField searchYear = new JTextField(6);
        JTextField searchMonth = new JTextField(4);
        RoundedButton searchBtn = new RoundedButton("Search", new Color(0, 123, 255), Color.WHITE);

        styleComboBox(searchType);
        searchYear.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchMonth.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchYear.setPreferredSize(new Dimension(80, 38));
        searchMonth.setPreferredSize(new Dimension(70, 38));

        filterPanel.add(new JLabel("Filter Type:"));
        filterPanel.add(searchType);
        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(searchYear);
        filterPanel.add(new JLabel("Month (1-12):"));
        filterPanel.add(searchMonth);
        filterPanel.add(searchBtn);

        searchBtn.addActionListener(e -> loadTableData(
                searchType.getSelectedItem().toString(),
                searchYear.getText().trim(),
                searchMonth.getText().trim()));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setOpaque(false);

        RoundedButton editBtn = new RoundedButton("Edit ", new Color(255, 193, 7), Color.DARK_GRAY);
        RoundedButton deleteBtn = new RoundedButton("Delete ", new Color(220, 53, 69), Color.WHITE);
        editBtn.setPreferredSize(new Dimension(140, 38));
        deleteBtn.setPreferredSize(new Dimension(150, 38));

        editBtn.addActionListener(e -> handleEditAction());
        deleteBtn.addActionListener(e -> handleDeleteAction());

        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);

        searchRow.add(filterPanel, BorderLayout.WEST);
        searchRow.add(actionPanel, BorderLayout.EAST);

        String[] columns = { "ID", "Date", "Type", "Amount", "Note" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
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

        tablePanel.add(searchRow, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void handleEditAction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to edit.",
                    "Selection Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Transaction t = service.getTransactionById(id);

            if (t == null) {
                JOptionPane.showMessageDialog(this, "Transaction not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create Edit Form Dialog
            JComboBox<String> typeBox = new JComboBox<>(new String[] { "Income", "Expense" });
            typeBox.setSelectedItem(t.getType());
            JTextField amountField = new JTextField(String.valueOf(t.getAmount()));
            JTextField dateField = new JTextField(t.getDate().toString());
            JTextField noteField = new JTextField(t.getNote());

            JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
            panel.add(new JLabel("Type:"));
            panel.add(typeBox);
            panel.add(new JLabel("Amount:"));
            panel.add(amountField);
            panel.add(new JLabel("Date (YYYY-MM-DD):"));
            panel.add(dateField);
            panel.add(new JLabel("Note:"));
            panel.add(noteField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Transaction",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                double newAmount = Double.parseDouble(amountField.getText().trim());
                if (newAmount <= 0)
                    throw new NumberFormatException();
                if (!DateUtils.isValidDate(dateField.getText().trim()))
                    throw new IllegalArgumentException("Invalid date format.");

                service.updateTransaction(id, typeBox.getSelectedItem().toString(), newAmount,
                        Date.valueOf(dateField.getText().trim()), noteField.getText().trim());
                refreshData();
                JOptionPane.showMessageDialog(this, "✅ Transaction updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number for amount.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating transaction: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteAction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.",
                    "Selection Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this transaction?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteTransaction(id);
                refreshData();
                JOptionPane.showMessageDialog(this, "✅ Transaction deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting transaction: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshData() {
        checkAndArchiveYear();
        loadTableData("All", "", "");
        updateStatistics();
        refreshArchivePanel();
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