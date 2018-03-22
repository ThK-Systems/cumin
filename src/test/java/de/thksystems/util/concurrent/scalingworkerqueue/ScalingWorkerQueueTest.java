/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */

package de.thksystems.util.concurrent.scalingworkerqueue;

import java.util.Arrays;

import org.junit.Test;

public class ScalingWorkerQueueTest {

    @Test
    public void testScalingWorkerQueue() {
        @SuppressWarnings("unchecked") DefaultConfiguredScalingWorkerQueue<Long> swq = new DefaultConfiguredScalingWorkerQueue((n) -> Arrays.asList(1L, 6L, 33L, 57L, -45L), (e, c) -> sleepWithoutException(212L));
        swq.start();
        sleepWithoutException(6_000L);
        swq.stop();
        sleepWithoutException(3_000L);
    }

    private void sleepWithoutException(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            // IGNORE
        }
    }

}