package de.thksystems.util.concurrent;

/**
 * A {@link Runnable} with the ability to set the thread-name that is used while running.
 */
public class NamedRunnable implements Runnable {

    private final Runnable runnable;
    private String threadName;

    protected NamedRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public static NamedRunnable of(Runnable runnable) {
        return new NamedRunnable(runnable);
    }

    @Override
    public void run() {
        String oldThreadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(threadName);
            runnable.run();
        } finally {
            Thread.currentThread().setName(oldThreadName);
        }
    }

    public NamedRunnable withThreadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

}
