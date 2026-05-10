package be.esi.prj.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:external-data/borrow_manager.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        initDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // =====================================================
    // CONNECTION
    // =====================================================
    public Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            return conn;

        } catch (SQLException e) {
            throw new RuntimeException("DB connection error", e);
        }
    }

    // =====================================================
    // INIT DATABASE
    // =====================================================
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