/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.network;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public final class DNSUtils {

    private static DirContext dnsDirContext = null;

    private DNSUtils() {
    }

    private static DirContext getDnsDirContext() throws NamingException {
        if (dnsDirContext == null) {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            dnsDirContext = new InitialDirContext(env);
        }
        return dnsDirContext;
    }

    /**
     * Get domain-records for hostname.
     * <p>
     * Adapted from http://mowyourlawn.com/files/RecordType.java.txt.
     */
    public static List<String> lookup(String hostName, RecordType record) {

        List<String> result = new Vector<>();
        try {
            Attributes attrs = getDnsDirContext().getAttributes(hostName, new String[]{record.name()});
            Attribute attr = attrs.get(record.name());
            if (attr != null) {
                NamingEnumeration<?> attrEnum = attr.getAll();
                while (attrEnum.hasMoreElements()) {
                    result.add(attrEnum.next().toString());
                }
            }
        } catch (NamingException e) {
            return new Vector<>();
        }
        return result;
    }

    /**
     * Get all domain-records for hostname.
     */
    public static List<String> lookupAll(String hostName) {

        List<String> result = new Vector<>();
        for (RecordType drt : RecordType.values()) {
            result.addAll(lookup(hostName, drt));
        }
        return result;
    }

    /**
     * Checks, if the given hostname matches the given IP-address-string.
     */
    public static boolean isMatchingIpAddress(String hostName, String ipAddr) {
        List<String> allIps = lookupAll(hostName);
        for (String ip : allIps) {
            if (ip.equals(ipAddr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for existing host-name.
     */
    public static boolean exists(String hostName) {
        for (RecordType drt : RecordType.values()) {
            List<String> result = lookup(hostName, drt);
            if (result.size() > 0) {
                return true;
            }
        }
        return false;
    }

    public enum RecordType {
        A, CNAME, NS, MX, SOA
    }
}
