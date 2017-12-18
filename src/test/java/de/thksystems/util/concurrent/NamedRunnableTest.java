/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NamedRunnableTest {

    @Test
    public void test() throws Exception {
        final String threadName = "FooBar-Thread-42";
        NamedRunnable.of(() -> assertEquals(threadName, Thread.currentThread().getName())).withThreadName(threadName);
        Thread.sleep(50);
    }
}
