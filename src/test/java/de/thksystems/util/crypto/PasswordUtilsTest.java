package de.thksystems.util.crypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PasswordUtilsTest {

    @Test
    public void testCreatePasswordHash() {
        assertNotEquals("Geheim", PasswordUtils.createPasswordHash("Geheim"));
        assertNotNull(PasswordUtils.createPasswordHash("FooBar42"));
        assertNull(PasswordUtils.createPasswordHash(null));
        assertNull(PasswordUtils.createPasswordHash(""));

        String run1 = PasswordUtils.createPasswordHash("VeryS3cret!");
        String run2 = PasswordUtils.createPasswordHash("VeryS3cret!");
        assertEquals(run1, run2);
    }

}
