/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.concurrent;

import java.util.function.Consumer;

/**
 * A {@link Consumer} with the ability to set the thread name that is used while calling.
 */
public class NamedConsumer<V> implements Consumer<V> {

    private final Consumer<V> consumer;
    private String threadName;

    protected NamedConsumer(Consumer<V> consumer) {
        this.consumer = consumer;
    }

    public static <V> NamedConsumer<V> of(Consumer<V> consumer) {
        return new NamedConsumer<>(consumer);
    }

    @Override
    public void accept(V t) {
        String oldThreadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(threadName.replace("{thread-id}", String.valueOf(Thread.currentThread().getId())));
            consumer.accept(t);
        } finally {
            Thread.currentThread().setName(oldThreadName);
        }
    }

}
