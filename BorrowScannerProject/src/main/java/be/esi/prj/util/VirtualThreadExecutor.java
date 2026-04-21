package be.esi.prj.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class VirtualThreadExecutor {

    // Executor basé sur les virtual threads (Java 21+)
    private static final ExecutorService EXECUTOR =
            Executors.newVirtualThreadPerTaskExecutor();

    // Empêche l’instanciation
    private VirtualThreadExecutor() {}

    /**
     * Exécute une tâche en asynchrone avec un virtual thread
     */
    public static void run(Runnable task) {
        EXECUTOR.submit(task);
    }
}