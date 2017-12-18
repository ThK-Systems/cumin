package de.thksystems.util.function;

@FunctionalInterface
public interface CheckedConsumer<T, X extends Throwable> {

	void accept(T t) throws X;
}