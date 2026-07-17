package src.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        try (Connection conn = DBConnection.getBaseConnection();
                Statement stmt = conn.createStatement()) {

            // Create Database
            stmt.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS moneymap_db");

            // Connect Database
            try (Connection dbConn = DBConnection.getConnection();
                    Statement dbStmt = dbConn.createStatement()) {

                // Users Table
                String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "full_name VARCHAR(100) NOT NULL,"
                        + "username VARCHAR(50) NOT NULL UNIQUE,"
                        + "password VARCHAR(255) NOT NULL"
                        + ")";

                dbStmt.executeUpdate(usersTable);

                // Transactions Table
                String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "user_id INT NOT NULL,"
                        + "type VARCHAR(20) NOT NULL,"
                        + "amount DECIMAL(15,2) NOT NULL,"
                        + "trans_date DATE NOT NULL,"
                        + "note VARCHAR(255),"
                        + "FOREIGN KEY(user_id) REFERENCES users(id)"
                        + " ON DELETE CASCADE"
                        + ")";

                dbStmt.executeUpdate(transactionsTable);

                System.out.println(
                        "Database initialized successfully");

            }

        } catch (Exception e) {

            e.printStackTrace();

            System.err.println(
                    "Database initialization failed");
        }
    }
}