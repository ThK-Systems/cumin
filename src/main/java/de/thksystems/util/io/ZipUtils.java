/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public final class ZipUtils {

    private ZipUtils() {
    }

    /**
     * Writes the given data zipped to a {@link File}.
     *
     * @param file         {@link File} to write the zipped {@link String} to.
     * @param zipEntryName The data will be written to a virtual file ({@link ZipEntry}) inside the zip. This is the name of this file
     * @param data         data to be zipped.
     */
    public static void zipDataToFile(File file, String zipEntryName, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ZipOutputStream zos = new ZipOutputStream(fos);
        ZipEntry ze = new ZipEntry(zipEntryName);
        zos.putNextEntry(ze);
        InputStream is = new ByteArrayInputStream(data);
        IOUtils.copy(is, zos);
        is.close();
        zos.closeEntry();
        zos.close();
    }

    /**
     * Writes the given {@link String} zipped to a {@link File}.
     *
     * @see #zipDataToFile(File, String, byte[])
     */
    public static void zipStringToFile(File file, String zipEntryName, String stringToZip) throws IOException {
        zipDataToFile(file, zipEntryName, stringToZip.getBytes());
    }

}
