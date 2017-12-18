/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */

package de.thksystems.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Creates a new {@link ArrayList} with the given entries.
     */
    @SafeVarargs
    public static <T> List<T> createArrayList(T... entries) {
        return addToCollection(new ArrayList<>(), entries);
    }

    /**
     * Adds all entries to the given {@link Collection}.
     */
    @SafeVarargs
    public static <T, C extends Collection<T>> C addToCollection(C collection, T... entries) {
        Collections.addAll(collection, entries);
        return collection;
    }

}
