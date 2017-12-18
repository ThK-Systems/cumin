/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.concurrent;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

public class NamedCallableTest {

    @Test
    public void testForegroundCall() throws Exception {
        String threadName = "FooBar-Thread-42";
        NamedCallable<String> nc = NamedCallable.of(() -> Thread.currentThread().getName()).withThreadName(threadName);
        assertEquals(threadName, nc.call());
    }

    @Test
    public void testWithThread() throws Exception {
        String threadName = "MyThread-23";
        ExecutorService executorService = Executors.newCachedThreadPool();
        assertEquals(threadName, executorService.submit(NamedCallable.of(() -> Thread.currentThread().getName()).withThreadName(threadName)).get());
    }

}
