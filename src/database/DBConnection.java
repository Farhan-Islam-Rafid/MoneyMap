package src.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // We connect to the MySQL server first without a database to ensure we can
    // create it
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "moneymap_db";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getBaseConnection() throws SQLException {
        return DriverManager.getConnection(BASE_URL, USER, PASS);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(BASE_URL + DB_NAME, USER, PASS);
    }
}