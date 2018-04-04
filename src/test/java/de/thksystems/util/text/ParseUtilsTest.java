package de.thksystems.util.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ParseUtilsTest {

    @Test
    public void testParseFilesize() {
        assertEquals(50L, ParseUtils.parseFileSize("50").longValue());
        assertEquals(73L, ParseUtils.parseFileSize("73.8").longValue());
        assertNull(ParseUtils.parseFileSize("73,8"));
        assertNull(ParseUtils.parseFileSize("-55K"));
        assertNull(ParseUtils.parseFileSize("ABC"));
        assertNull(ParseUtils.parseFileSize("5A"));
        assertEquals(Math.round(0.8 * 1024L), ParseUtils.parseFileSize("0.8k").longValue());
        assertEquals(85L * 1024L * 1024L, ParseUtils.parseFileSize("85MB").longValue());
        assertEquals(Math.round(43.67 * 1024L * 1024L * 1024L), ParseUtils.parseFileSize("43.67Gb").longValue());
        assertEquals(1099511627776L, ParseUtils.parseFileSize("1tB").longValue());
        assertEquals(33776997205278L, ParseUtils.parseFileSize("0.03E").longValue());
        assertNull(ParseUtils.parseFileSize(""));
        assertNull(ParseUtils.parseFileSize("    "));
        assertNull(ParseUtils.parseFileSize(null));
        assertNull(ParseUtils.parseFileSize("-50"));
    }

    @Test
    public void testParseDuration() {
        assertEquals(50L, ParseUtils.parseDuration("50").longValue());
        assertEquals(50L, ParseUtils.parseDuration("50ms").longValue());
        assertEquals(50L, ParseUtils.parseDuration("0.05s").longValue());
        assertEquals(2_500L, ParseUtils.parseDuration(" 2.5s").longValue());
        assertEquals(1000 * 60 * 7L, ParseUtils.parseDuration("7m").longValue());
        assertEquals(1000 * 60 * 60 * 3L, ParseUtils.parseDuration("3h ").longValue());
        assertEquals(1000 * 60 * 60 * 24L, ParseUtils.parseDuration("1d").longValue());

        assertEquals(1000 * 60 * 3L + 55 * 1000L, ParseUtils.parseDuration("3m 55s").longValue());
        assertEquals(1000 * 60 * 3L + 55 * 1000L, ParseUtils.parseDuration(" 3m55s ").longValue());
        assertEquals(1000 * 60 * 60 * 4L + 7, ParseUtils.parseDuration("4h 7").longValue());
        assertEquals(1000 * 60 * 60 * 18L + 1000 * 60 * 3L + 6 * 1000L, ParseUtils.parseDuration("18h 3m6s").longValue());

        assertNull(ParseUtils.parseDuration(""));
        assertNull(ParseUtils.parseDuration(" "));
        assertNull(ParseUtils.parseDuration(null));

        assertEquals(Long.MAX_VALUE, ParseUtils.parseDuration(" ∞ ").longValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseDurationError1() {
        assertEquals(1000 * 60 * 3L + 55 * 1000L, ParseUtils.parseDuration("55s 3m").longValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseDurationError2() {
        assertEquals(1000 * 60 * 3L + 55 * 1000L, ParseUtils.parseDuration("3m 60s").longValue());
    }

}
