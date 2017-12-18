package de.thksystems.util.function;

@FunctionalInterface
public interface CheckedRunnable<X extends Throwable> {

    void run() throws X;

}