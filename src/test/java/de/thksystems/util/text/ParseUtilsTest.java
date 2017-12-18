package de.thksystems.util.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ParseUtilsTest {

	@Test
	public void testParseFilesize() {
		assertEquals(50l, ParseUtils.parseFileSize("50").longValue());
		assertEquals(73l, ParseUtils.parseFileSize("73.8").longValue());
		assertNull(ParseUtils.parseFileSize("73,8"));
		assertNull(ParseUtils.parseFileSize("-55K"));
		assertNull(ParseUtils.parseFileSize("ABC"));
		assertNull(ParseUtils.parseFileSize("5A"));
		assertEquals(Math.round(0.8 * 1024l), ParseUtils.parseFileSize("0.8k").longValue());
		assertEquals(85l * 1024l * 1024l, ParseUtils.parseFileSize("85MB").longValue());
		assertEquals(Math.round(43.67 * 1024l * 1024l * 1024l), ParseUtils.parseFileSize("43.67Gb").longValue());
		assertEquals(1099511627776l, ParseUtils.parseFileSize("1tB").longValue());
		assertEquals(33776997205278l, ParseUtils.parseFileSize("0.03E").longValue());
		assertNull(ParseUtils.parseFileSize(""));
		assertNull(ParseUtils.parseFileSize(null));
		assertNull(ParseUtils.parseFileSize("-50"));
	}

	@Test
	public void testParseDuration() {
		assertEquals(50l, ParseUtils.parseDuration("50").longValue());
		assertEquals(50l, ParseUtils.parseDuration("50ms").longValue());
		assertEquals(50l, ParseUtils.parseDuration("0.05s").longValue());
		assertEquals(1000 * 60 * 7l, ParseUtils.parseDuration("7m").longValue());
		assertEquals(1000 * 60 * 60 * 3l, ParseUtils.parseDuration("3h").longValue());
		assertEquals(1000 * 60 * 60 * 24 * 1l, ParseUtils.parseDuration("1d").longValue());

		assertEquals(1000 * 60 * 3l + 55 * 1000l, ParseUtils.parseDuration("3m 55s").longValue());
		assertEquals(1000 * 60 * 3l + 55 * 1000l, ParseUtils.parseDuration("3m55s").longValue());
		assertEquals(1000 * 60 * 60 * 4l + 7, ParseUtils.parseDuration("4h 7").longValue());
		assertEquals(1000 * 60 * 60 * 18l + 1000 * 60 * 3l + 6 * 1000l, ParseUtils.parseDuration("18h 3m6s").longValue());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseDurationError1() {
		assertEquals(1000 * 60 * 3l + 55 * 1000l, ParseUtils.parseDuration("55s 3m").longValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseDurationError2() {
		assertEquals(1000 * 60 * 3l + 55 * 1000l, ParseUtils.parseDuration("3m 60s").longValue());
	}

}
