/*
 * tksCommons
 * 
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.concurrent;

import java.util.concurrent.Callable;

/**
 * A {@link Callable} with the ability to set the thread name that is used while calling.
 */
public class NamedCallable<V> implements Callable<V> {

	private final Callable<V> callable;
	private String threadName;

	protected NamedCallable(Callable<V> callable) {
		this.callable = callable;
	}

	@Override
	public V call() throws Exception {
		String oldThreadName = Thread.currentThread().getName();
		try {
			Thread.currentThread().setName(threadName.replace("{thread-id}", String.valueOf(Thread.currentThread().getId())));
			V result = callable.call();
			return result;
		} finally {
			Thread.currentThread().setName(oldThreadName);
		}
	}

	public static <V> NamedCallable<V> of(Callable<V> callable) {
		return new NamedCallable<>(callable);
	}

	/**
	 * Sets the thread name.
	 * 
	 * If the thread name may contains "{thread-id}", it will be substituted with the id of the thread.
	 */
	public NamedCallable<V> withThreadName(String threadName) {
		this.threadName = threadName;
		return this;
	}

}