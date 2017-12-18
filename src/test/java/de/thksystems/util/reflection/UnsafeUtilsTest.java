package de.thksystems.util.reflection;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class UnsafeUtilsTest {

	@Test
	public void testGetUnsafe() {
		assertNotNull(UnsafeUtils.getUnsafe());
	}

}
