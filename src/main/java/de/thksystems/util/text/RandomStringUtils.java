/*
 * tksCommons
 * 
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) 
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.text;

import org.apache.commons.text.RandomStringGenerator;

public final class RandomStringUtils {

	private static RandomStringGenerator rsgAlphanumeric = new RandomStringGenerator.Builder().withinRange('a', 'z').withinRange('0', '9').build();

	private RandomStringUtils() {
	}

	public static String randomAlphanumeric(int length) {
		return rsgAlphanumeric.generate(length);
	}

}
