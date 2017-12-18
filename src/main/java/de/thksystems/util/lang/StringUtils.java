/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.lang;

public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Extract all capitals (uppercase characters) of a string.
     * <ul>
     * <li>"hello" -> ""</li>
     * <li>"Hello" -> "H"</li>
     * <li>"HelLo" -> "HL"</li>
     * <li>"hello World. Here I am." -> "WHI"</li>
     * <li>null -> null</li>
     */
    public static String getCapitals(String s) {
        if (s == null) {
            return null;
        }
        // @formatter:off
        return s.chars().filter(Character::isUpperCase)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
        // @formatter:on
    }

}
