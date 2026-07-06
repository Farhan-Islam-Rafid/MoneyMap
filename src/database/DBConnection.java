package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DBConnection is responsible ONLY for establishing and providing
 * a connection to the MoneyMap MySQL database.
 *
 * It also makes sure the required "transactions" table exists,
 * creating it automatically the first time the app connects.
 */
public class DBConnection {

    // ------------------------------------------------------------------
    // Update these values to match your local MySQL setup if needed.
    // ------------------------------------------------------------------
    private static final String URL = "jdbc:mysql://localhost:3306/MoneyMap?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    // Prevent instantiation - this is a utility class.
    private DBConnection() {
    }

    /**
     * Returns a live connection to the database, creating a new one
     * (and the transactions table, if missing) when necessary.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. " +
                        "Make sure mysql-connector-j is on the classpath.", e);
            }
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            createTableIfNotExists(connection);
        }
        return connection;
    }

    /**
     * Creates the "transactions" table automatically if it does not
     * already exist, so the app works right out of the box.
     */
    private static void createTableIfNotExists(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "type VARCHAR(20) NOT NULL, " +
                "amount DOUBLE NOT NULL, " +
                "transaction_date DATE NOT NULL" +
                ")";
        try (Statement statement = conn.createStatement()) {
            statement.execute(sql);
        }
    }

    /** Closes the shared connection, if open. Call on app shutdown. */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
