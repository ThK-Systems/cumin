package de.thksystems.util.concurrent;

public class ThreadUtils {

    private ThreadUtils() {
    }

    /**
     * Calling {@link Thread#sleep(long)} by <b>ignoring</b> the {@link InterruptedException}.
     * <p>
     * Use with care!
     */
    public static void sleepWithoutException(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            // IGNORE
        }
    }

}
