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
    private volatile boolean initialized = false;
    private final Supplier<T> resultSupplier;
    private T result = null;

    public Deferred(Supplier<T> resultSupplier) {
        this.resultSupplier = Objects.requireNonNull(resultSupplier);
    }

    public T get() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) { // Double safety
                    result = resultSupplier.get();
                    initialized = true;
                }
            }
        }
        return result;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void invalidate() {
        initialized = false;
    }

    @Override
    public String toString() {
        if (!initialized) {
            return super.toString();
        } else {
            return getClass().getSimpleName() + "[" + result.toString() + "]";
        }
    }
}