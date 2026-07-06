package ui;

import model.Transaction;
import service.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MainFrame contains the entire MoneyMap user interface.
 * It talks to the database only through TransactionService,
 * keeping UI code and database code cleanly separated.
 */
public class MainFrame extends JFrame {

    // ---- Color theme -------------------------------------------------
    private static final Color COLOR_BACKGROUND = new Color(245, 246, 250);
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_PRIMARY = new Color(41, 98, 255);
    private static final Color COLOR_INCOME = new Color(34, 153, 84);
    private static final Color COLOR_EXPENSE = new Color(214, 69, 69);
    private static final Color COLOR_TEXT_MUTED = new Color(110, 118, 135);

    private static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_BALANCE = new Font("Segoe UI", Font.BOLD, 36);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    // ---- Data / logic --------------------------------------------------
    private final TransactionService transactionService = new TransactionService();

    // ---- UI components referenced after creation ------------------------
    private JLabel balanceValueLabel;
    private JTextField amountField;
    private JComboBox<String> typeComboBox;
    private DefaultTableModel tableModel;
    private JTable historyTable;

    public MainFrame() {
        super("MoneyMap");
        initFrame();
        buildUI();
        refreshData();
    }

    private void initFrame() {
        setSize(800, 600);
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null); // center the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(COLOR_BACKGROUND);
        setLayout(new BorderLayout());
    }

    private void buildUI() {
        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------
    // Header: app title + current balance
    // ------------------------------------------------------------------
    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BACKGROUND);
        header.setBorder(new EmptyBorder(24, 30, 10, 30));

        JLabel titleLabel = new JLabel("MoneyMap");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(new Color(30, 34, 45));

        JPanel balanceCard = new JPanel();
        balanceCard.setLayout(new BoxLayout(balanceCard, BoxLayout.Y_AXIS));
        balanceCard.setBackground(COLOR_CARD);
        balanceCard.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel balanceCaption = new JLabel("Current Balance");
        balanceCaption.setFont(FONT_BASE);
        balanceCaption.setForeground(COLOR_TEXT_MUTED);
        balanceCaption.setAlignmentX(Component.RIGHT_ALIGNMENT);

        balanceValueLabel = new JLabel("৳ 0.00");
        balanceValueLabel.setFont(FONT_BALANCE);
        balanceValueLabel.setForeground(COLOR_PRIMARY);
        balanceValueLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        balanceCard.add(balanceCaption);
        balanceCard.add(balanceValueLabel);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(balanceCard, BorderLayout.EAST);
        return header;
    }

    // ------------------------------------------------------------------
    // Center: input form (top) + transaction history table (bottom)
    // ------------------------------------------------------------------
    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(COLOR_BACKGROUND);
        center.setBorder(new EmptyBorder(10, 30, 24, 30));

        center.add(buildInputCard(), BorderLayout.NORTH);
        center.add(buildHistoryCard(), BorderLayout.CENTER);
        return center;
    }

    private JPanel buildInputCard() {
        JPanel card = roundedCard();
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Amount label + field
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setFont(FONT_BASE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        card.add(amountLabel, gbc);

        amountField = new JTextField();
        amountField.setFont(FONT_BASE);
        amountField.setPreferredSize(new Dimension(180, 34));
        gbc.gridx = 1;
        gbc.weightx = 1;
        card.add(amountField, gbc);

        // Type label + combo box
        JLabel typeLabel = new JLabel("Type");
        typeLabel.setFont(FONT_BASE);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(typeLabel, gbc);

        typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});
        typeComboBox.setFont(FONT_BASE);
        gbc.gridx = 3;
        gbc.weightx = 1;
        card.add(typeComboBox, gbc);

        // Add button
        RoundedButton addButton = new RoundedButton("Add");
        addButton.addActionListener(e -> onAddTransaction());
        gbc.gridx = 4;
        gbc.weightx = 0;
        card.add(addButton, gbc);

        // Pressing Enter in the amount field also adds the transaction
        amountField.addActionListener(e -> onAddTransaction());

        return card;
    }

    private JPanel buildHistoryCard() {
        JPanel wrapper = roundedCard();
        wrapper.setLayout(new BorderLayout(0, 10));

        JLabel historyLabel = new JLabel("Transaction History");
        historyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        historyLabel.setForeground(new Color(30, 34, 45));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Type", "Amount", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFont(FONT_BASE);
        historyTable.setRowHeight(28);
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        historyTable.getTableHeader().setBackground(new Color(235, 238, 245));
        historyTable.setSelectionBackground(new Color(225, 233, 250));
        historyTable.setShowGrid(false);
        historyTable.setIntercellSpacing(new Dimension(0, 0));

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        wrapper.add(historyLabel, BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    /** A plain white "card" panel with padding, used to group related controls. */
    private JPanel roundedCard() {
        JPanel card = new JPanel();
        card.setBackground(COLOR_CARD);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        return card;
    }

    // ------------------------------------------------------------------
    // Button behaviour
    // ------------------------------------------------------------------
    private void onAddTransaction() {
        String rawAmount = amountField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();

        // 1. Validate the amount
        if (rawAmount.isEmpty()) {
            showError("Amount cannot be empty.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(rawAmount);
        } catch (NumberFormatException ex) {
            showError("Amount must be a valid number.");
            return;
        }

        if (amount < 0) {
            showError("Amount cannot be negative.");
            return;
        }
        if (amount == 0) {
            showError("Amount must be greater than zero.");
            return;
        }

        // 2 & 3 already done above (read + validate). Now save.
        try {
            if ("Income".equals(type)) {
                transactionService.addIncome(amount);
            } else {
                transactionService.addExpense(amount);
            }
        } catch (SQLException ex) {
            showError("Could not save the transaction.\n" + ex.getMessage());
            return;
        }

        // 5, 6, 7: reload table, recalc balance, clear input
        refreshData();
        amountField.setText("");
        amountField.requestFocusInWindow();
    }

    // ------------------------------------------------------------------
    // Data refresh helpers
    // ------------------------------------------------------------------
    private void refreshData() {
        refreshBalance();
        refreshHistory();
    }

    private void refreshBalance() {
        try {
            double balance = transactionService.getCurrentBalance();
            balanceValueLabel.setText(String.format("৳ %,.2f", balance));
            balanceValueLabel.setForeground(balance < 0 ? COLOR_EXPENSE : COLOR_PRIMARY);
        } catch (SQLException ex) {
            showError("Could not calculate balance.\n" + ex.getMessage());
        }
    }

    private void refreshHistory() {
        try {
            List<Transaction> transactions = transactionService.loadHistory();
            tableModel.setRowCount(0); // clear existing rows
            for (Transaction t : transactions) {
                tableModel.addRow(new Object[]{
                        t.getId(),
                        t.getType(),
                        String.format("৳ %,.2f", t.getAmount()),
                        t.getTransactionDate().format(DATE_FORMAT)
                });
            }
        } catch (SQLException ex) {
            showError("Could not load transaction history.\n" + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "MoneyMap", JOptionPane.ERROR_MESSAGE);
    }
}
