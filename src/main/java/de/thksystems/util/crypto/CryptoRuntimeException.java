/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.crypto;

public class CryptoRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -3245562020723219672L;

    public CryptoRuntimeException() {
    }

    public CryptoRuntimeException(String msg) {
        super(msg);
    }

    public CryptoRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoRuntimeException(Throwable cause) {
        super(cause);
    }

}
