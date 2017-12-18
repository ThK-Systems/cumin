/*
 * tksCommons
 * 
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */

package de.thksystems.util.collection;

import java.util.HashMap;

public final class MapUtils {

	private MapUtils() {
	}

	/**
	 * Creates a {@link HashMap} of {@link String},{@link String} by reading key-value-pairs like 'key::value'.
	 */
	public static HashMap<String, String> createHashMap(String... values) {
		return createHashMapWithDelimiter("::", values);
	}

	/**
	 * Creates a {@link HashMap} of {@link String},{@link String} by reading key-value-pairs like 'key[delimiter]value'.
	 */
	public static HashMap<String, String> createHashMapWithDelimiter(String delimiter, String... values) {
		HashMap<String, String> map = new HashMap<>(values.length);
		for (String value : values) {
			String[] pair = value.split(delimiter);
			if (pair.length != 2 || pair[0].length() == 0 || pair[1].length() == 0) {
				throw new IllegalArgumentException("Invalid key-value-pair: " + value);
			}
			if (map.containsKey(pair[0])) {
				throw new IllegalArgumentException("Duplicate key: " + pair[0]);
			}
			map.put(pair[0], pair[1]);
		}
		return map;
	}

}
