package de.thksystems.util.function;

@FunctionalInterface
public interface CheckedFunction<T, R, X extends Throwable> {

    R apply(T t) throws X;

}
