/*
 * tksCommons
 *
 *  Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */

package de.thksystems.util.concurrent.scalingworkerqueue;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DefaultConfiguredScalingWorkerQueue<E> extends ScalingWorkerQueue<E, DefaultWorkerQueueConfiguration> {

    public DefaultConfiguredScalingWorkerQueue(Function<Integer, Collection<E>> supplier, BiConsumer<E, DefaultWorkerQueueConfiguration> worker) {
        super(new DefaultWorkerQueueConfiguration(), supplier, worker);
    }
}
