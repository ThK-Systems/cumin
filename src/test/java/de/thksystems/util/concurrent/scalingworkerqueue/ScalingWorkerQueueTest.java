/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */

package de.thksystems.util.concurrent.scalingworkerqueue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ScalingWorkerQueueTest {

    @Test
    public void testScalingWorkerQueue() {
        @SuppressWarnings("unchecked") DefaultConfiguredScalingWorkerQueue<Long> swq = new DefaultConfiguredScalingWorkerQueue((n) -> Arrays.asList(1L, 6L, 33L, 57L, -45L), (e, c) -> sleepWithoutException(212L));
        swq.start();
        sleepWithoutException(6_000L);
        swq.stop();
        sleepWithoutException(3_000L);
    }

    @Test
    public void testDieingIdleWorker() {
        Function<Integer, Collection<Long>> supplierFunction = new Function<Integer, Collection<Long>>() {
            boolean first = true;

            @Override
            public Collection<Long> apply(Integer count) {
                if(first) {
                    first = false;
                    return Arrays.asList(1L, 6L, 33L, 57L, -45L, 456_123L);
                }
                return Collections.emptyList();
            }
        };

        @SuppressWarnings("unchecked") DefaultConfiguredScalingWorkerQueue<Long> swq = new DefaultConfiguredScalingWorkerQueue(supplierFunction, (e, c) -> sleepWithoutException(212L));
        swq.start();
        sleepWithoutException(40_000L);
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