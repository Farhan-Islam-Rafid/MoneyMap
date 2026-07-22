package src.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static final Properties properties = new Properties();

    static {
        try {
            FileInputStream file = new FileInputStream("config.properties");
            properties.load(file);
            file.close();
        } catch (IOException e) {
            throw new RuntimeException("config.properties file not found!");
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
