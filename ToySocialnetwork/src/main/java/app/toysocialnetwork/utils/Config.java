package app.toysocialnetwork.utils;

import java.io.IOException;
import java.util.Properties;

public class Config {
    public static Properties getProperties() throws RuntimeException {
        try {
            Properties properties = new Properties();
            properties.load(Config.class.getResourceAsStream("/db.properties"));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        try {
            return getProperties().getProperty(key);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
