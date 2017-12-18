/*
 * tksCommons
 * 
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testGetCapitals() {
		assertEquals("", StringUtils.getCapitals("hello"));
		assertEquals("H", StringUtils.getCapitals("Hello"));
		assertEquals("HL", StringUtils.getCapitals("HelLo"));
		assertEquals("E", StringUtils.getCapitals("hEllo"));
		assertEquals("WHI", StringUtils.getCapitals("hello World. Here I am"));
		assertNull(StringUtils.getCapitals(null));
	}

}
