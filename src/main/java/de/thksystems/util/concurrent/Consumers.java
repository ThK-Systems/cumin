/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */

package de.thksystems.util.concurrent;

import java.util.function.Consumer;

public final class Consumers {

    private Consumers() {
    }

    // @formatter:off
    public static <T> Consumer<T> noOp() {
        return t-> {};
    }
    // @formatter:on
}
