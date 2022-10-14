/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */

package de.thksystems.util.math;

public final class MathUtils {

    private MathUtils() {
    }

    /**
     * Returns logarithm with given base of given value.
     */
    public static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }
}
