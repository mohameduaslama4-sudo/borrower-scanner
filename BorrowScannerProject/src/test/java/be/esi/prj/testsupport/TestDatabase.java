package be.esi.prj.testsupport;

import be.esi.prj.service.BorrowerService;
import be.esi.prj.service.ItemService;
import be.esi.prj.service.LoanService;
import be.esi.prj.util.DatabaseManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class TestDatabase {

    private TestDatabase() {
    }

    public static void reset() {
        try {
            Files.createDirectories(Path.of("external-data", "qrcodes"));
            DatabaseManager.getInstance();

            try (Connection connection = DatabaseManager.getInstance().getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute("DELETE FROM loans");
                statement.execute("DELETE FROM items");
                statement.execute("DELETE FROM borrowers");
                statement.execute("DELETE FROM sqlite_sequence WHERE name IN ('borrowers', 'loans')");
            }

            BorrowerService.getInstance().getBorrowersObservable().clear();
            ItemService.getInstance().getItemsObservable().clear();
            LoanService.getInstance().getLoansObservable().clear();
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Cannot reset test database", e);
        }
    }
}
