package src.service;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import src.database.DBConnection;
import src.model.Transaction;
import src.session.Session;

public class TransactionService {

    // Add Transaction
    public void addTransaction(String type, double amount, Date date, String note) throws SQLException {

        String query = "INSERT INTO transactions "
                + "(user_id, type, amount, trans_date, note) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Session.userId);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setDate(4, date);
            pstmt.setString(5, note);

            pstmt.executeUpdate();

        }
    }

    // Update Transaction
    public void updateTransaction(int id, String type, double amount, Date date, String note)
            throws SQLException {

        String query = "UPDATE transactions SET "
                + "type=?, amount=?, trans_date=?, note=? "
                + "WHERE id=? AND user_id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, type);
            pstmt.setDouble(2, amount);
            pstmt.setDate(3, date);
            pstmt.setString(4, note);
            pstmt.setInt(5, id);
            pstmt.setInt(6, Session.userId);

            pstmt.executeUpdate();

        }

    }

    // Delete Transaction
    public void deleteTransaction(int id) throws SQLException {

        String query = "DELETE FROM transactions "
                + "WHERE id=? AND user_id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.setInt(2, Session.userId);

            pstmt.executeUpdate();

        }

    }

    // Get Transaction By ID
    public Transaction getTransactionById(int id) throws SQLException {

        String query = "SELECT * FROM transactions "
                + "WHERE id=? AND user_id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.setInt(2, Session.userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                return new Transaction(

                        rs.getInt("id"),

                        rs.getString("type"),

                        rs.getDouble("amount"),

                        rs.getDate("trans_date"),

                        rs.getString("note")

                );

            }

        }

        return null;

    }

    // Get All Transactions
    public List<Transaction> getTransactions(
            String searchType,
            String searchYear,
            String searchMonth)
            throws SQLException {

        List<Transaction> list = new ArrayList<>();

        StringBuilder query = new StringBuilder(
                "SELECT * FROM transactions WHERE user_id=?");

        if (searchType != null && !searchType.equals("All")) {

            query.append(" AND type='")
                    .append(searchType)
                    .append("'");

        }

        if (searchYear != null && !searchYear.trim().isEmpty()) {

            query.append(" AND YEAR(trans_date)=")
                    .append(searchYear);

        }

        if (searchMonth != null && !searchMonth.trim().isEmpty()) {

            query.append(" AND MONTH(trans_date)=")
                    .append(searchMonth);

        }

        query.append(
                " ORDER BY trans_date DESC, id DESC");

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            pstmt.setInt(1, Session.userId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                list.add(new Transaction(

                        rs.getInt("id"),

                        rs.getString("type"),

                        rs.getDouble("amount"),

                        rs.getDate("trans_date"),

                        rs.getString("note")

                ));

            }

        }

        return list;

    }

    // Calculate Sum
    public double getSum(String type, LocalDate startDate, LocalDate endDate)
            throws SQLException {

        String query = "SELECT SUM(amount) as total "
                + "FROM transactions "
                + "WHERE type=? AND user_id=?";

        if (startDate != null && endDate != null) {

            query += " AND trans_date BETWEEN ? AND ?";

        } else if (startDate != null) {

            query += " AND trans_date=?";

        }

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, type);

            pstmt.setInt(2, Session.userId);

            if (startDate != null && endDate != null) {

                pstmt.setDate(3, Date.valueOf(startDate));
                pstmt.setDate(4, Date.valueOf(endDate));

            } else if (startDate != null) {

                pstmt.setDate(3, Date.valueOf(startDate));

            }

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                return rs.getDouble("total");

            }

        }

        return 0.0;

    }

}