/*
 * tksCommons
 * 
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.concurrent;

import java.util.function.Supplier;

/**
 * A {@link Supplier} with the ability to set the thread name that is used while calling.
 */
public class NamedSupplier<V> implements Supplier<V> {

	private final Supplier<V> supplier;
	private String threadName;

	protected NamedSupplier(Supplier<V> supplier) {
		this.supplier = supplier;
	}

	@Override
	public V get() {
		String oldThreadName = Thread.currentThread().getName();
		try {
			Thread.currentThread().setName(threadName.replace("{thread-id}", String.valueOf(Thread.currentThread().getId())));
			V result = supplier.get();
			return result;
		} finally {
			Thread.currentThread().setName(oldThreadName);
		}
	}

	public static <V> NamedSupplier<V> of(Supplier<V> supplier) {
		return new NamedSupplier<>(supplier);
	}

}
