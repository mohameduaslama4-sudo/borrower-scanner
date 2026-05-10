package be.esi.prj.util;

import be.esi.prj.testsupport.TestDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @BeforeEach
    void setUp() {
        TestDatabase.reset();
    }

    @Test
    void databaseManagerCreatesTablesAndEnablesForeignKeys() throws Exception {
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet foreignKeys = statement.executeQuery("PRAGMA foreign_keys");
            assertTrue(foreignKeys.next());
            assertEquals(1, foreignKeys.getInt(1));

            ResultSet tables = statement.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type = 'table'");
            Set<String> names = new java.util.HashSet<>();
            while (tables.next()) {
                names.add(tables.getString("name"));
            }

            assertTrue(names.contains("borrowers"));
            assertTrue(names.contains("items"));
            assertTrue(names.contains("loans"));
        }
    }

    @Test
    void virtualThreadExecutorRunsSubmittedTask() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> threadName = new AtomicReference<>();

        VirtualThreadExecutor.run(() -> {
            threadName.set(Thread.currentThread().toString());
            latch.countDown();
        });

        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertNotNull(threadName.get());
    }

    @Test
    void utilityClassesKeepPrivateConstructors() throws Exception {
        assertPrivateConstructor(VirtualThreadExecutor.class);
        assertPrivateConstructor(SceneManager.class);
    }

    private static void assertPrivateConstructor(Class<?> type) throws Exception {
        Constructor<?> constructor = type.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}
