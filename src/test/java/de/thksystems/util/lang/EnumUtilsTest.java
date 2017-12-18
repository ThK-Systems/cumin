package de.thksystems.util.lang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EnumUtilsTest {

	enum TestEnum {
		AA, BB, CC, DD
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testMatch() {
		assertTrue(EnumUtils.match(TestEnum.AA, TestEnum.AA, TestEnum.CC));
		assertTrue(EnumUtils.match(TestEnum.AA, TestEnum.AA));
		assertTrue(EnumUtils.match(TestEnum.AA, TestEnum.BB, TestEnum.CC, TestEnum.BB, TestEnum.AA));
		assertFalse(EnumUtils.match(TestEnum.AA, TestEnum.CC));
		assertFalse(EnumUtils.match(TestEnum.AA, TestEnum.CC, TestEnum.BB));
		assertFalse(EnumUtils.match(TestEnum.AA, TestEnum.BB, TestEnum.CC, TestEnum.BB));
	}

}
