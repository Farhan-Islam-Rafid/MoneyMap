package src.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection conn = DBConnection.getBaseConnection();
                Statement stmt = conn.createStatement()) {

            // Create Database
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS moneymap_db");

            // Connect to the specific database and create table
            try (Connection dbConn = DBConnection.getConnection();
                    Statement dbStmt = dbConn.createStatement()) {

                String createTableSQL = "CREATE TABLE IF NOT EXISTS transactions ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "type VARCHAR(10) NOT NULL, "
                        + "amount DECIMAL(15,2) NOT NULL, "
                        + "trans_date DATE NOT NULL, "
                        + "note VARCHAR(255) "
                        + ")";
                dbStmt.executeUpdate(createTableSQL);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize the database. Ensure MySQL is running.");
        }
    }
}