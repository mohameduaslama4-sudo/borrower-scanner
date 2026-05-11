package be.esi.prj.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

/**
 * Manages the SQLite database connection and initialization.
 * <p>
 * Responsible for creating the database file, ensuring required folders exist,
 * and initializing the database schema (tables and constraints).
 */
public class ConnectionManager {

    private static ConnectionManager instance;
    private Properties properties;
    private Connection connection;

    private ConnectionManager() {
        loadProperties();
        initDatabase();
    }

    /**
     * Returns the singleton instance of the ConnectionManager.
     *
     * @return the ConnectionManager instance
     */
    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    /**
     * Loads database configuration properties from the properties file.
     * <p>
     * This method reads the "database.properties" file and initializes
     * the {@link Properties} object used for database connection settings.
     *
     * @throws RuntimeException if the properties file cannot be read or found
     */
    private void loadProperties() {
        if (properties == null) {
            properties = new Properties();
            try (InputStream input = ConnectionManager.class.getClassLoader().getResourceAsStream("database.properties")) {
                properties.load(input);
            } catch (IOException e) {
                throw new RuntimeException("Properties file unreadable", e);
            }
        }
    }

    /**
     * Creates and returns a connection to the SQLite database.
     * <p>
     * Also enables foreign key constraints.
     *
     * @return a database connection
     * @throws RuntimeException if the connection fails
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.user");
                String password = properties.getProperty("db.password");

                connection = DriverManager.getConnection(url, user, password);

                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                }
            }
            return connection;

        } catch (SQLException e) {
                throw new RuntimeException("DB connection error", e);
        }
    }

    /**
     * Initializes the database structure at startup.
     * <p>
     * Creates the required folders and database tables if they do not exist.
     * This method is called once during instantiation.
     *
     * @throws RuntimeException if initialization fails
     */
    private void initDatabase() {

        try {
            // create folder if needed
            Files.createDirectories(Paths.get("external-data"));

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                // ================= BORROWERS =================
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS borrowers (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        first_name TEXT NOT NULL,
                        last_name TEXT NOT NULL,
                        email TEXT NOT NULL
                    )
                """);

                // ================= ITEMS =================
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS items (
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        status TEXT NOT NULL,
                        qr_path TEXT NOT NULL
                    )
                """);

                // ================= LOANS =================
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS loans (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        item_id TEXT NOT NULL,
                        borrower_id INTEGER NOT NULL,
                        borrowed_date TEXT NOT NULL,
                        due_date TEXT NOT NULL,
                        return_date TEXT,

                        FOREIGN KEY (item_id) REFERENCES items(id)
                            ON DELETE CASCADE
                            ON UPDATE CASCADE,

                        FOREIGN KEY (borrower_id) REFERENCES borrowers(id)
                            ON DELETE CASCADE
                            ON UPDATE CASCADE
                    )
                """);

            }

        } catch (Exception e) {
            throw new RuntimeException("DB initialization error", e);
        }
    }
}