package app.toysocialnetwork.utils;

public class NetworkDB extends DataBase {
    private final static NetworkDB instance = new NetworkDB();

    private NetworkDB() {
        super(
                Config.getProperty("db.url"),
                Config.getProperty("db.username"),
                Config.getProperty("db.password"));
    }

    public static NetworkDB getInstance() {
        return instance;
    }
}
