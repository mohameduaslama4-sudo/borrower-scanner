package be.esi.prj.testsupport;

import be.esi.prj.service.BorrowerService;
import be.esi.prj.service.ItemService;
import be.esi.prj.service.LoanService;
import be.esi.prj.util.ConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class TestDatabase {

    private static final Path QR_FOLDER = Path.of("external-data", "qrcodes");

    private TestDatabase() {
    }

    public static void reset() {
        try {
            // ensure QR folder exists
            Files.createDirectories(QR_FOLDER);

            // init DB
            ConnectionManager.getInstance();

            // clean database
            try (Connection connection = ConnectionManager.getInstance().getConnection();
                 Statement statement = connection.createStatement()) {

                statement.execute("DELETE FROM loans");
                statement.execute("DELETE FROM items");
                statement.execute("DELETE FROM borrowers");
                statement.execute(
                        "DELETE FROM sqlite_sequence WHERE name IN ('borrowers', 'loans')"
                );
            }

            // clear service caches
            BorrowerService.getInstance().getBorrowersObservable().clear();
            ItemService.getInstance().getItemsObservable().clear();
            LoanService.getInstance().getLoansObservable().clear();

        } catch (SQLException | IOException e) {
            throw new RuntimeException("Cannot reset test database", e);
        }
    }
}