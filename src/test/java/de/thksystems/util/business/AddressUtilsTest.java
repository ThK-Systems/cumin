package de.thksystems.util.business;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Test;

public class AddressUtilsTest {

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testExtractHousenumber() {
        testExtractHousenumber("Konrad Adenauer Platz 1 ", "Konrad Adenauer Platz", "1");
        testExtractHousenumber("Ludwig-Erhard-Straße 3b", "Ludwig-Erhard-Straße", "3b");
        testExtractHousenumber("Ludwig-Erhard-Straße 3 b", "Ludwig-Erhard-Straße", "3 b");
        testExtractHousenumber("Helmut Kohl Weg", "Helmut Kohl Weg", null);
        testExtractHousenumber("50th George Washington Avenue", "George Washington Avenue", "50th");
    }

    protected void testExtractHousenumber(String fullStreet, String expectedStreet, String expectedHousenumber) {
        Street sh1 = AddressUtils.extractHousenumber(fullStreet);
        assertEquals(expectedStreet, sh1.getStreet());
        assertEquals(expectedHousenumber, sh1.getHouseNumber());
    }

}
