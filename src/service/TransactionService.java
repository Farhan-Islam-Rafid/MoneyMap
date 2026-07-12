package src.service;

import src.database.DBConnection;
import src.model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    public void addTransaction(String type, double amount, Date date, String note) throws SQLException {
        String query = "INSERT INTO transactions (type, amount, trans_date, note) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, type);
            pstmt.setDouble(2, amount);
            pstmt.setDate(3, date);
            pstmt.setString(4, note);
            pstmt.executeUpdate();
        }
    }

    public List<Transaction> getTransactions(String searchType, String searchYear, String searchMonth)
            throws SQLException {
        List<Transaction> list = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE 1=1");

        if (searchType != null && !searchType.equals("All")) {
            query.append(" AND type = '").append(searchType).append("'");
        }
        if (searchYear != null && !searchYear.trim().isEmpty()) {
            query.append(" AND YEAR(trans_date) = ").append(searchYear);
        }
        if (searchMonth != null && !searchMonth.trim().isEmpty()) {
            query.append(" AND MONTH(trans_date) = ").append(searchMonth);
        }
        query.append(" ORDER BY trans_date DESC, id DESC");

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query.toString())) {

            while (rs.next()) {
                list.add(new Transaction(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getDate("trans_date"),
                        rs.getString("note")));
            }
        }
        return list;
    }

    public double getSum(String type, LocalDate startDate, LocalDate endDate) throws SQLException {
        String query = "SELECT SUM(amount) as total FROM transactions WHERE type = ?";
        if (startDate != null && endDate != null) {
            query += " AND trans_date BETWEEN ? AND ?";
        } else if (startDate != null) {
            query += " AND trans_date = ?";
        }

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, type);
            if (startDate != null && endDate != null) {
                pstmt.setDate(2, Date.valueOf(startDate));
                pstmt.setDate(3, Date.valueOf(endDate));
            } else if (startDate != null) {
                pstmt.setDate(2, Date.valueOf(startDate));
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }
}