/*
 *
 *  * tksCommons
 *  *
 *  * Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 *  * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 *
 */

package de.thksystems.util.concurrent.scalingworkerqueue;

public interface QueueConfiguration {

    long getDispatcherWaitPeriod();

    int getElementsPerRunner();

    int getMinElementsToSupply();

    int getMaxRunner();

    int getMinRunner();

    int getSupplySpareElementCount();

    long getRunnerWaitIdle();

    long getRunnerMaxWaitIdle();
}
