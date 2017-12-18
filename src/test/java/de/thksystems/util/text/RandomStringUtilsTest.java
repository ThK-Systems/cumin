package de.thksystems.util.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RandomStringUtilsTest {

    @Test
    public void testRandomAlphanumeric() {
        String s = RandomStringUtils.randomAlphanumeric(20);
        assertEquals(20, s.length());
    }

}
