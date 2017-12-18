/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.lang;

import java.util.Collection;

public final class EnumUtils {

    private EnumUtils() {
    }

    /**
     * Returns <code>true</code>, if the entry is part of list.
     *
     * @deprecated Use {@link Collection#contains(Object)}
     */
    @Deprecated
    @SafeVarargs
    public static <E> boolean match(E entry, E... list) {
        if (list.length == 0) {
            return false;
        }
        for (E listEntry : list) {
            if (listEntry == entry) {
                return true;
            }
        }
        return false;
    }

}
