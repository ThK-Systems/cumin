/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.concurrent;

import java.util.function.Function;

/**
 * A {@link Function} with the ability to set the thread name that is used while calling.
 */
public class NamedFunction<V, R> implements Function<V, R> {

    private final Function<V, R> function;
    private String threadName;

    protected NamedFunction(Function<V, R> function) {
        this.function = function;
    }

    public static <V, R> NamedFunction<V, R> of(Function<V, R> function) {
        return new NamedFunction<>(function);
    }

    @Override
    public R apply(V t) {
        String oldThreadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(threadName.replace("{thread-id}", String.valueOf(Thread.currentThread().getId())));
            R result = function.apply(t);
            return result;
        } finally {
            Thread.currentThread().setName(oldThreadName);
        }
    }

}
