/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.business;

import java.io.Serializable;

public class Street implements Serializable {

    private static final long serialVersionUID = -6460377478480898042L;

    private final String street;

    private final String houseNumber;

    public Street(String street, String houseNumber) {
        super();
        this.street = street.trim();
        if (houseNumber != null) {
            this.houseNumber = houseNumber.trim();
        } else {
            this.houseNumber = null;
        }
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

}
