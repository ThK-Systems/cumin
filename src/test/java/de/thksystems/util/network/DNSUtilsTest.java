package de.thksystems.util.network;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DNSUtilsTest {

	@Test
	public void testExists() {
		assertTrue(DNSUtils.exists("www.google.com"));
		assertTrue(DNSUtils.exists("google")); // Really !!!
		assertFalse(DNSUtils.exists("thk-systems")); // May be in future ... ;)
	}

	@Test
	public void testIsMatchingIpAddress() {
		assertTrue(DNSUtils.isMatchingIpAddress("www.thk-systems.de", "81.169.229.223"));
		assertFalse(DNSUtils.isMatchingIpAddress("www.google.com", "10.10.10.10"));
	}
}
