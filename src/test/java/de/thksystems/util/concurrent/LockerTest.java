/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

import org.junit.Ignore;
import org.junit.Test;

public class LockerTest {

    @Test
    public void testLockAndUnlock() throws Exception {
        final Locker<String> locker = new Locker<String>();

        locker.lock("LOCKME");
        assertFalse(locker.isLocked("LOCKME"));
        assertTrue(locker.isHeldByCurrentThread("LOCKME"));

        Thread1 thread1 = new Thread1(locker);
        thread1.start();
        thread1.join();
        assertTrue(thread1.result);

        locker.unlock("LOCKME");
        assertFalse(locker.isLocked("LOCKME"));
        assertFalse(locker.isHeldByCurrentThread("LOCKME"));

        Thread2 thread2 = new Thread2(locker);
        thread2.start();
        thread2.join();
        assertTrue(thread2.result);

    }

    @Test
    public void testLockCount() throws Exception {
        final Locker<BigDecimal> locker = new Locker<BigDecimal>();
        BigDecimal bd = new BigDecimal("1500");

        locker.lock(bd);
        locker.lock(bd);
        assertTrue(locker.isHeldByCurrentThread(bd));

        locker.unlock(bd);
        assertTrue(locker.isHeldByCurrentThread(bd));

        locker.unlock(bd);
        assertFalse(locker.isHeldByCurrentThread(bd));
    }

    @Test
    public void testLockOrder() throws Exception {
        final Locker<String> locker = new Locker<String>();

        ThreadQueued[] threads = new ThreadQueued[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new ThreadQueued(locker);
            threads[i].start();
            Thread.sleep(50);
        }

        for (int i = 0; i < 10; i++) {
            assertTrue(String.valueOf(i), threads[i].isRunning);
            threads[i].runme = false;
            threads[i].join();
            Thread.sleep(50);
        }
    }

    @Test
    public void testExecuteWithLock() throws Exception {
        Locker<String> locker = new Locker<>();
        String elem = "LOCKME";
        locker.lock(elem);
        // ... with Runnable
        locker.executeWithLock(elem, () -> System.out.println("Hello: " + elem));
        // ... with Supplier
        long result = locker.executeWithLock(elem, () -> 5L);
        assertEquals(5L, result);
        assertTrue(locker.isHeldByCurrentThread(elem));
    }

    @Test
    @Ignore
    public void testWithGarbageCollection() throws Exception {
        Locker<String> locker = new Locker<>();
        String elem = "LOCKME" + System.currentTimeMillis();
        locker.lock(elem);

        WeakReference<String> ref = new WeakReference<String>(elem.intern());
        elem = null;
        while (ref.get() != null) {
            System.gc();
        }

        assertTrue(locker.isHeldByCurrentThread("LOCKME"));
    }

    private final class Thread1 extends Thread {
        private final Locker<String> tl;

        Boolean result = null;

        private Thread1(Locker<String> tl) {
            this.tl = tl;
        }

        @Override
        public void run() {
            try {
                tl.unlock("LOCKME");
                tl.lock("LOCK2");
                tl.lock("LOCK2");
                tl.lock("LOCK2");
                tl.unlock("LOCK2");
                tl.unlock("LOCK2");
                tl.unlock("LOCK2");
                tl.lock("LOCKME", 1L);
                result = false;
            } catch (TimeoutException e) {
                result = true;
            }
        }
    }

    private final class Thread2 extends Thread {
        private final Locker<String> locker;

        Boolean result = null;

        private Thread2(Locker<String> locker) {
            this.locker = locker;
        }

        @Override
        public void run() {
            locker.lock("LOCKME");
            result = true;
            locker.unlock("LOCKME");
        }
    }

    private final class ThreadQueued extends Thread {

        private final Locker<String> locker;

        private boolean runme = true;

        private boolean isRunning = false;

        public ThreadQueued(Locker<String> locker) {
            this.locker = locker;
        }

        @Override
        public void run() {
            locker.lock("LOCKME");
            isRunning = true;
            while (runme) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            }
            locker.unlock("LOCKME");
        }
    }
}
