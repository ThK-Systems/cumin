package de.thksystems.util.function;

@FunctionalInterface
public interface CheckedBiFunction<T, U, R, X extends Throwable> {

	R apply(T t, U u) throws X;

}