package service;

import database.DBConnection;
import model.Transaction;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TransactionService contains all the database access logic for
 * MoneyMap: inserting income/expense rows, loading transaction
 * history, and calculating the current balance.
 *
 * All queries use PreparedStatement - never a plain Statement.
 */
public class TransactionService {

    private static final String TYPE_INCOME = "Income";
    private static final String TYPE_EXPENSE = "Expense";

    /** Inserts a new Income row dated today. */
    public void addIncome(double amount) throws SQLException {
        insertTransaction(TYPE_INCOME, amount);
    }

    /** Inserts a new Expense row dated today. */
    public void addExpense(double amount) throws SQLException {
        insertTransaction(TYPE_EXPENSE, amount);
    }

    private void insertTransaction(String type, double amount) throws SQLException {
        String sql = "INSERT INTO transactions (type, amount, transaction_date) VALUES (?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, type);
            statement.setDouble(2, amount);
            statement.setDate(3, Date.valueOf(LocalDate.now()));
            statement.executeUpdate();
        }
    }

    /** Returns every transaction, newest first (highest id first). */
    public List<Transaction> loadHistory() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, type, amount, transaction_date " +
                "FROM transactions ORDER BY id DESC";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        resultSet.getInt("id"),
                        resultSet.getString("type"),
                        resultSet.getDouble("amount"),
                        resultSet.getDate("transaction_date").toLocalDate()
                );
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    /** Sums all amounts for the given type (Income or Expense). Never returns null. */
    private double getTotalByType(String type) throws SQLException {
        String sql = "SELECT SUM(amount) AS total FROM transactions WHERE type = ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, type);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double total = resultSet.getDouble("total");
                    // SUM() returns NULL when there are no matching rows.
                    return resultSet.wasNull() ? 0.0 : total;
                }
            }
        }
        return 0.0;
    }

    /**
     * Calculates the current balance on the fly.
     * Current Balance = Total Income - Total Expense.
     * The balance itself is never stored in the database.
     */
    public double getCurrentBalance() throws SQLException {
        double totalIncome = getTotalByType(TYPE_INCOME);
        double totalExpense = getTotalByType(TYPE_EXPENSE);
        return totalIncome - totalExpense;
    }
}
