package app.toysocialnetwork.utils;

import java.io.IOException;
import java.util.Properties;

public class Config {
    /**
     * Get the properties from the db.properties file
     * @return Properties
     * @throws RuntimeException if the file is not found
     */
    public static Properties getProperties() throws RuntimeException {
        try {
            Properties properties = new Properties();
            properties.load(Config.class.getResourceAsStream("/db.properties"));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the property value from the db.properties file
     * @param key the property key
     * @return the property value
     */
    public static String getProperty(String key) {
        try {
            return getProperties().getProperty(key);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
