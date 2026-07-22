package src.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnection {

    private static final HikariDataSource dataSource;

    static {

        System.out.println("Starting HikariCP...");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(Config.get("DB_URL"));
        config.setUsername(Config.get("DB_USER"));
        config.setPassword(Config.get("DB_PASSWORD"));

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(3);
        config.setConnectionTimeout(10000);

        System.out.println("Creating datasource...");

        dataSource = new HikariDataSource(config);

        System.out.println("Datasource created!");
    }

    public static Connection getConnection() throws SQLException {

        System.out.println("Trying database connection...");

        Connection con = dataSource.getConnection();

        System.out.println("Database connected!");

        return con;
    }
}
