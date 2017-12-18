package de.thksystems.util.function;

@FunctionalInterface
public interface CheckedFunction<R, T, X extends Throwable> {

	R apply(T t) throws X;

}
