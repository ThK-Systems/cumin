/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * Tools to encrypt passwords.
 */
public final class PasswordUtils {

    public static final String DEFAULT_PASSWORD_HASHALGO = "SHA-256";

    private PasswordUtils() {
    }

    /**
     * Creates a hash of the given password using the given hash algorithm.
     *
     * @return Hex string of password hash.
     * @throws NoSuchAlgorithmException Hash algorithm is not available
     */
    public static String createPasswordHash(String plainPassword, String hashAlgo) throws NoSuchAlgorithmException {
        if (plainPassword == null || plainPassword.isEmpty()) {
            return null;
        }
        MessageDigest md = MessageDigest.getInstance(hashAlgo);
        byte[] digest = md.digest(plainPassword.getBytes());
        byte[] digestHex = new Hex(StandardCharsets.UTF_8).encode(digest);
        return new String(digestHex);
    }

    /**
     * Creates a hash of the given password using {@value #DEFAULT_PASSWORD_HASHALGO}.
     *
     * @return Hex string of password hash.
     */
    public static String createPasswordHash(String plainPassword) {
        try {
            return createPasswordHash(plainPassword, DEFAULT_PASSWORD_HASHALGO);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoRuntimeException(e); // Should not happen
        }
    }

    /**
     * Encrypts given password using SHA-256 and encodes Base64 .
     *
     * @deprecated Use {@link #createPasswordHash(String)}
     */
    @Deprecated
    public static String encryptAsPassword(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(DEFAULT_PASSWORD_HASHALGO);
            byte[] digest = md.digest(password.getBytes());
            return new String(new Base64().encode(digest));
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoRuntimeException(e);
        }
    }
}
