/*
 * tksCommons
 * 
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.lang;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Lazy initialization using a {@link Supplier} (thread-safe).
 * 
 * @see "Inspired by http://www.nosid.org/java8-threadsafe-lazy-initialization.html"
 */
public final class Deferred<T> {
	private volatile Supplier<T> resultSupplier = null;
	private T result = null;

	public Deferred(Supplier<T> resultSupplier) {
		this.resultSupplier = Objects.requireNonNull(resultSupplier);
	}

	public T get() {
		if (resultSupplier != null) {
			synchronized (this) {
				if (resultSupplier != null) { // Double safety
					result = resultSupplier.get();
					resultSupplier = null;
				}
			}
		}
		return result;
	}

	public boolean isInitialized() {
		return resultSupplier == null;
	}
}