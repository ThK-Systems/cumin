package de.thksystems.util.function;

@FunctionalInterface
public interface CheckedSupplier<S, X extends Throwable> {

    S get() throws X;
}