/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.business;

import org.apache.commons.lang3.StringUtils;

public final class AddressUtils {

    private AddressUtils() {
    }

    /**
     * Extracts the housenumber from a given street.
     * <p>
     * <b>Warning: Not tested against all possible street-formats in the world!!</b>
     */
    public static Street extractHousenumber(String fullStreetName) {
        String[] streetArray = fullStreetName.split("\\s");
        for (int i = 0; i < streetArray.length; i++) {
            String streetItem = streetArray[i];
            if (streetItem.substring(0, 1).matches("\\d")) {
                switch (i) {
                    case 0:
                        return new Street(StringUtils.join(streetArray, " ", i + 1, streetArray.length), streetItem);
                    default:
                        return new Street(StringUtils.join(streetArray, " ", 0, i), StringUtils.join(streetArray, " ", i, streetArray.length));
                }
            }
        }
        return new Street(fullStreetName, null);
    }
}
