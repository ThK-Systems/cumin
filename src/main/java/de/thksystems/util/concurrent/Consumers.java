/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */

package de.thksystems.util.concurrent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Consumers {

    private Consumers() {
    }

    public static <T> Consumer<T> noOp() {
        return t-> {};
    }

    public static <T, S> BiConsumer<T, S> noBiOp() {
        return (t,s)-> {};
    }

}
