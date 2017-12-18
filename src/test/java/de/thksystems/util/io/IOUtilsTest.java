package de.thksystems.util.io;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

public class IOUtilsTest {

    @Test
    public void testCopyStringWriter() {
        String myText = "FooBar42";
        StringWriter sw = new StringWriter();
        sw.write(myText);
        assertEquals(myText, IOUtils.copyStringWriter(sw).toString());
    }

}
