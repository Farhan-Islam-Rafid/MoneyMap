package src.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement()) {

            // Users Table
            String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id SERIAL PRIMARY KEY,"
                    + "full_name VARCHAR(100) NOT NULL,"
                    + "username VARCHAR(50) UNIQUE NOT NULL,"
                    + "password VARCHAR(255) NOT NULL"
                    + ")";

            stmt.executeUpdate(usersTable);

            // Transactions Table
            String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                    + "id SERIAL PRIMARY KEY,"
                    + "user_id INT NOT NULL,"
                    + "type VARCHAR(20) NOT NULL,"
                    + "amount DECIMAL(15,2) NOT NULL,"
                    + "trans_date DATE NOT NULL,"
                    + "note VARCHAR(255),"
                    + "FOREIGN KEY(user_id) REFERENCES users(id)"
                    + " ON DELETE CASCADE"
                    + ")";

            stmt.executeUpdate(transactionsTable);

            System.out.println(
                    "Neon PostgreSQL Database initialized successfully");

        } catch (Exception e) {

            e.printStackTrace();

            System.err.println(
                    "Database initialization failed" + e);

        }

    }

}
