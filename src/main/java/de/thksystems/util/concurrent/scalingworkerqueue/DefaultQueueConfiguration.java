/*
 *
 *  * tksCommons
 *  *
 *  * Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 *  * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 *
 */

package de.thksystems.util.concurrent.scalingworkerqueue;

public class DefaultQueueConfiguration implements QueueConfiguration {

    @Override
    public long getDispatcherWaitPeriod() {
        return 500;
    }

    @Override
    public int getElementsPerRunner() {
        return 2;
    }

    @Override
    public int getMinElementsToSupply() {
        return 10;
    }

    @Override
    public int getMaxRunner() {
        return 10;
    }

    @Override
    public int getMinRunner() {
        return 1;
    }

    @Override
    public int getSupplySpareElementCount() {
        return getElementsPerRunner();
    }

    @Override
    public long getRunnerWaitIdle() {
        return 500;
    }

    @Override
    public long getRunnerMaxWaitIdle() {
        return 30_000;
    }

}
