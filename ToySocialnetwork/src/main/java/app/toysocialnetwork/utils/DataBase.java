package app.toysocialnetwork.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructor for DataBase class
     * @param url the url of the database
     * @param username the username of the database
     * @param password the password of the database
     */
    public DataBase(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Method to get a connection to the database
     * @return a connection to the database
     * @throws RuntimeException if there is an error getting the connection
     */
    public Connection getConnection() throws RuntimeException {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
