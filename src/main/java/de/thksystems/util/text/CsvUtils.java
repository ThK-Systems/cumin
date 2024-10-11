/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.text;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * Handle CSV.
 */
public final class CsvUtils {

    private CsvUtils() {
    }

    /**
     * Returns the CSV-file from the input stream as a list of string-arrays.
     */
    public static List<String[]> getAsList(InputStream is, char separator) {
        String sepStr = String.valueOf(separator);
        List<String> lines = IOUtils.readLines(is, Charset.defaultCharset());
        List<String[]> csvList = new ArrayList<>(lines.size());
        for (String line : lines) {
            if (line != null && !line.isEmpty()) {
                csvList.add(line.split(sepStr));
            }
        }
        return csvList;
    }
}
