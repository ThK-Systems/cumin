/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */

package de.thksystems.util.concurrent.scalingworkerqueue;

public interface WorkerQueueConfiguration {

    /**
     * Period (in ms) the dispatcher sleeps between checks (for new elements, to stop, ...)
     */
    long getDispatcherSleepPeriod();

    /**
     * Period (ms) to wait on empty fetch.
     */
    long getDispatcherWaitPeriodOnEmptyFetch();

    /**
     * Count of elements per runner.
     */
    int getCountOfElementsPerRunner();

    /**
     * Minimum count of elements to supply (independent of count of runners).
     */
    int getMinElementsCountToSupply();

    /**
     * Get count of spare elements to supply beyond the count of elements given by the count of running runners.
     */
    int getSpareElementsCountToSupply();

    /**
     * Minimum count of runners.
     */
    int getMinRunnerCount();

    /**
     * Maximum count of runners.
     */
    int getMaxRunnerCount();

    /**
     * Period (in ms) the runners sleeps, if it is idle. (Before it tries to get the next element.)
     */
    long getRunnerSleepIdlePeriod();

    /**
     * Maximum idle period (in ms) of a runner. If this period is exceeded, the runner is stopped (if possible).
     */
    long getRunnerMaxIdlePeriod();
}
