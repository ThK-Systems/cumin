/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.concurrent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thksystems.util.function.CheckedSupplier;

/**
 * Locking util.
 */
public final class Locker<T> {

    private static final Logger LOG = LoggerFactory.getLogger(Locker.class);

    /**
     * Queue of threads for element. The first entry is the current locking one. Accesses to this map must be synchronized.
     */
    private final Map<T, Queue<Thread>> threadQueueMap = new HashMap<>();

    /**
     * Count of locks by current locking thread.
     */
    private final Map<T, Long> lockCounts = new HashMap<>();

    /**
     * Gets the waiting queue for the given element.
     */
    private synchronized Queue<Thread> getThreadQueueForElement(T element, boolean addIfMissing) {
        LOG.trace("Getting thread-queue for element: {}. Add if missing: {}", element, addIfMissing);
        Queue<Thread> threadQueue = threadQueueMap.get(element);
        if (threadQueue == null && addIfMissing) {
            LOG.trace("Adding thread-queue for element: {}", element);
            threadQueue = new LinkedList<>();
            threadQueueMap.put(element, threadQueue);
            lockCounts.put(element, 1L);
        }
        return threadQueue;
    }

    /**
     * Tries to lock the given element. It will be locked, if it is not locked by another thread.
     *
     * @return <code>true</code> in case of a succeeded lock, <code>false</code> otherwise
     */
    public boolean tryLock(T element) {
        try {
            return lock(element, Optional.empty(), Optional.of(Boolean.TRUE));
        } catch (TimeoutException e) {
            // Must not happen here
            String msg = "Unexpected timeout exception: " + e.getMessage();
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Tries to lock the given element. It will be locked, if it is not locked by another thread.<br>
     * If it is locked, by another element, the supplied exception is thrown.
     */
    public <E extends Exception> void tryLock(T element, Supplier<E> exceptionSupplier) throws E {
        boolean lockSucceeded = tryLock(element);
        if (!lockSucceeded) {
            LOG.trace("Try lock failed. Throwing exception.");
            throw exceptionSupplier.get();
        }
    }

    /**
     * {@link Locker#lock(Object, long)} using an (almost) infinite waiting time.
     */
    public void lock(T element) {
        try {
            lock(element, Optional.empty(), Optional.empty());
        } catch (TimeoutException e) {
            // Must not happen here
            String msg = "Unexpected timeout exception: " + e.getMessage();
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * {@link Locker#lock(Object, Optional, Optional)} with a mandatory waiting time in milliseconds.
     */
    public void lock(T element, long maxWaitTime) throws TimeoutException {
        lock(element, Optional.of(maxWaitTime), Optional.empty());
    }

    /**
     * Locks the given element for the current thread. <br>
     * If it is already locked (for another thread), it waits for unlock.<br>
     * If the waiting time exceeds the given one, an {@link TimeoutException} is thrown.
     */
    @SuppressWarnings("unchecked")
    protected boolean lock(T element, Optional<Long> optionalMaxWaitTime, Optional<Boolean> tryLock) throws TimeoutException {
        LOG.debug("Locking: {}", element);

        // We need a unique string (if element if of type String)
        if (element instanceof String) {
            element = (T) ((String) element).intern(); // NOSONAR
        }

        // Adding to thread queue; This creates the (first) lock, if the queue was empty before
        addToThreadQueue(element);

        // Check for lock
        long startTime = System.currentTimeMillis();
        if (isLocked(element)) {
            if (tryLock.orElse(Boolean.FALSE)) {
                removeFromThreadQueueUnsafe(element);
                LOG.info("Element '{}' is already locked by: {}", element, getLockingThread(element));
                return false;
            }
            LOG.info("Waiting for lock of '{}'. Locked by: {}", element, getLockingThread(element));
        }
        // Wait for lock (by other thread), if any
        long maxWaitTime = optionalMaxWaitTime.orElse(1_000L * 60L * 24L * 365L * 1_000_000_000L); // ~1 billion years
        while (isLocked(element) && (System.currentTimeMillis() <= startTime + maxWaitTime)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Interruption here is okay.
            }
        }
        // Check if time exceeded while waiting on lock
        if (isLocked(element) && (System.currentTimeMillis() > startTime + maxWaitTime)) {
            removeFromThreadQueueUnsafe(element);
            String msg = String.format("Time (%d ms) exceeded for waiting on locked '%s'. Locked by: %s", maxWaitTime, element, getLockingThread(element));
            LOG.info(msg);
            throw new TimeoutException(msg);
        }
        return true; // Element was locked
    }

    /**
     * Add current thread to queue of waiting threads for given element.
     */
    protected void addToThreadQueue(T element) {
        // Create map entry in waiting queue for element, if needed
        Queue<Thread> threadQueue = getThreadQueueForElement(element, true);
        synchronized (threadQueue) {
            // Check, if element is locked by current thread, then increase counter
            if (isHeldByCurrentThread(element)) {
                lockCounts.merge(element, 1L, (old, inc) -> old + inc);
                LOG.debug("Element is already locked by current thread. Increased lock count: {}", lockCounts.get(element));
            }
            // If element is not held by current thread, add it to the waiting queue.
            else {
                Thread currentThread = Thread.currentThread();
                threadQueue.add(currentThread);
                LOG.trace("Added thread '{}' to waiting queue for '{}'", currentThread, element);
            }
        }
    }

    private void removeFromThreadQueueUnsafe(T element) {
        Queue<Thread> threadQueue = getThreadQueueForElement(element, false);
        synchronized (threadQueue) {
            Thread currentThread = Thread.currentThread();
            LOG.trace("Removing thread '{}' from waiting queue for '{}'", currentThread, element);
            threadQueue.remove(currentThread);
        }
    }

    /**
     * Unlocks the given element. (If it is not locked by the current thread, it will not be unlocked. No exception is thrown in this case, just logging.)
     * <p>
     * It is null-safe, because it may be used in finally blocks.
     */
    public void unlock(T element) {
        if (element == null) {
            return;
        }
        LOG.debug("Unlocking: {}", element);
        Queue<Thread> threadQueue = getThreadQueueForElement(element, false);
        if (threadQueue == null) {
            LOG.warn("The element '{}' is NOT locked!", element);
            return;
        }
        synchronized (threadQueue) {
            Thread lockingThread = threadQueue.peek();
            Thread currentThread = Thread.currentThread();
            if (lockingThread != currentThread) {
                LOG.warn("The element '{}' is NOT locked by the current thread '{}'. It is locked by thread '{}' -> IGNORED!", element, currentThread,
                        lockingThread);
                return;
            }
            if (lockCounts.get(element) == 1) {
                threadQueue.remove();
                LOG.trace("Unlocked.");
            } else {
                lockCounts.merge(element, 1L, (old, dec) -> old - dec);
                LOG.debug("Unlocking not possible, because locked more than onced. Decreased lock counter to {}", lockCounts.get(element));
            }
        }
    }

    /**
     * Returns <code>true</code>, if locked (by another thread).
     */
    public boolean isLocked(T element) {
        if (threadQueueMap.containsKey(element)) {
            Thread thread = threadQueueMap.get(element).peek();
            return thread != null && thread != Thread.currentThread();
        }
        return false;
    }

    /**
     * Return <code>true</code>, if locked by current thread.
     */
    public boolean isHeldByCurrentThread(T element) {
        if (threadQueueMap.containsKey(element)) {
            Thread thread = threadQueueMap.get(element).peek();
            return thread != null && thread == Thread.currentThread();
        }
        return false;
    }

    /**
     * Get currently locking thread.
     */
    public Thread getLockingThread(T element) {
        return threadQueueMap.containsKey(element) ? threadQueueMap.get(element).peek() : null;
    }

    /**
     * Locks element, than executes given {@link Runnable} and finally unlocks element. (Execute-around-method-pattern.)
     */
    public void executeWithLock(T element, Runnable task) {
        lock(element);
        try {
            task.run();
        } finally {
            unlock(element);
        }
    }

    /**
     * Try to lock element using {@link #tryLock(Object)}. If succeeded call onNotLocked {@link Runnable}, if locked call onLocked {@link Runnable}.
     */
    public void executeWithLock(T element, Runnable onNotLocked, Runnable onLocked) {
        try {
            if (tryLock(element)) {
                onNotLocked.run();
            } else {
                onLocked.run();
            }
        } finally {
            unlock(element);
        }
    }

    /**
     * Try to lock element using {@link #tryLock(Object)}. If succeeded call {@link Runnable}, if locked throw Exception.
     */
    public <X extends Exception> void executeWithLock(T element, Runnable task, X exception) throws X {
        try {
            if (tryLock(element)) {
                task.run();
            } else {
                throw exception;
            }
        } finally {
            unlock(element);
        }
    }

    /**
     * Locks element, than executes given {@link Supplier}, returns its result and finally unlocks element. (Execute-around-method-pattern.)
     */
    public <S> S executeWithLock(T element, Supplier<S> supplier) {
        lock(element);
        try {
            return supplier.get();
        } finally {
            unlock(element);
        }
    }

    /**
     * Try to lock element using {@link #tryLock(Object)}. If succeeded call onNotLocked {@link Supplier}, if locked call onLocked {@link Supplier}.
     */
    public <S> S executeWithLock(T element, Supplier<S> onNotLocked, Supplier<S> onLocked) {
        try {
            if (tryLock(element)) {
                return onNotLocked.get();
            } else {
                return onLocked.get();
            }
        } finally {
            unlock(element);
        }
    }

    /**
     * Try to lock element using {@link #tryLock(Object)}. If succeeded call {@link Supplier}, if locked throw Exception.
     */
    public <S, X extends Exception> S executeWithLock(T element, Supplier<S> supplier, X exception) throws X {
        try {
            if (tryLock(element)) {
                return supplier.get();
            } else {
                throw exception;
            }
        } finally {
            unlock(element);
        }
    }

    /**
     * Try to lock element using {@link #tryLock(Object)}. If succeeded call {@link CheckedSupplier}, if locked throw Exception.
     */
    public <S, X extends Exception, Y extends Exception> S executeCheckedWithLock(T element, CheckedSupplier<S, Y> supplier, X exception) throws X, Y {
        try {
            if (tryLock(element)) {
                return supplier.get();
            } else {
                throw exception;
            }
        } finally {
            unlock(element);
        }
    }

}
