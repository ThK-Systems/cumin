package de.thksystems.util.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ZipUtilsTest {

    @Test
    public void testZipStringToFile() throws IOException {
        File tmpFile = File.createTempFile(getClass().getName(), "test");

        String zipEntryName = "entry";
        String stringToZip = "This is a test";
        ZipUtils.zipStringToFile(tmpFile, zipEntryName, stringToZip);

        ZipInputStream zis = new ZipInputStream(new FileInputStream(tmpFile));
        ZipEntry entry = zis.getNextEntry();
        assertNotNull(entry);
        assertEquals(zipEntryName, entry.getName());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(zis, bos);
        assertEquals(stringToZip, bos.toString());
        assertNull(zis.getNextEntry());
        zis.close();
        bos.close();
    }
}
