/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.io;

import java.io.StringWriter;

public final class IOUtils {

    private IOUtils() {
    }

    /**
     * Copies the given {@link StringWriter}, by creating a new {@link StringWriter} and copying the string content.
     */
    public static StringWriter copyStringWriter(StringWriter sourceWriter) {
        sourceWriter.flush();
        StringWriter targetWriter = new StringWriter();
        targetWriter.write(sourceWriter.toString());
        targetWriter.flush();
        return targetWriter;
    }

}
