/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.io;

import java.io.Closeable;
import java.io.IOException;
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

    /**
     * Closes the {@link Closeable} (e.g. a {@link java.io.InputStream or a {@link java.io.OutputStream}}) without throwing (or logging any exception)!
     * <p>
     * <i>Use with care!</i>
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (IOException e) {
        }
    }

}
