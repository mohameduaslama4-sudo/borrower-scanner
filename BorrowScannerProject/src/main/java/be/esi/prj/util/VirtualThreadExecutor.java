package be.esi.prj.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utility class that provides an executor based on Java virtual threads.
 * <p>
 * Used to run asynchronous tasks efficiently without blocking the main thread.
 */
public final class VirtualThreadExecutor {

    // Executor based on virtual threads
    private static final ExecutorService EXECUTOR =
            Executors.newVirtualThreadPerTaskExecutor();

    private VirtualThreadExecutor() {}

    /**
     * Executes a task asynchronously using a virtual thread.
     *
     * @param task the task to execute
     */
    public static void run(Runnable task) {
        EXECUTOR.submit(task);
    }
}