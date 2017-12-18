package de.thksystems.util.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

public class MapUtilsTest {

    @Test
    public void testCreateHashMap_Successfull() {
        Map<String, String> map = MapUtils.createHashMap("Foo::Bar", "Homer::Simpson");
        assertEquals(2, map.size());
        assertTrue(map.containsKey("Foo"));
        assertEquals("Simpson", map.get("Homer"));
    }

    @Test
    public void testCreateHashMap_FailureDuplicate() {
        try {
            MapUtils.createHashMap("Foo::Bar", "Foo::Test");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testCreateHashMap_FailureInvalidKVP() {
        try {
            MapUtils.createHashMapWithDelimiter("@@@", "Foo::Bar", "Foo::Test");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

}
