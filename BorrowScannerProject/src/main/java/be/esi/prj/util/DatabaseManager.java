package be.esi.prj.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_PATH = "external-data/borrow_manager.db";
    private static final String URL = "jdbc:sqlite:" +DB_PATH;
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            createTables(); // On s'assure que les tables existent
        }
        return connection;
    }

    private static void createTables() {
        String itemsTable = "CREATE TABLE IF NOT EXISTS items (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "category TEXT, " +
                "status TEXT, " +
                "qr_path TEXT);";

        String borrowersTable = "CREATE TABLE IF NOT EXISTS borrowers (" +
                "id INTEGER PRIMARY KEY, " +
                "first_name TEXT NOT NULL, " +
                "last_name TEXT NOT NULL, " +
                "email TEXT);";

        String loansTable = "CREATE TABLE IF NOT EXISTS loans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "item_id TEXT NOT NULL, " +
                "borrower_id INTEGER NOT NULL, " +
                "borrowed_date TEXT NOT NULL, " +
                "due_date TEXT NOT NULL, " +
                "return_date TEXT, " +
                "FOREIGN KEY(item_id) REFERENCES items(id), " +
                "FOREIGN KEY(borrower_id) REFERENCES borrowers(id));";

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(itemsTable);
            stmt.execute(borrowersTable);
            stmt.execute(loansTable);
            stmt.execute("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
