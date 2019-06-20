/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.lang;

import java.util.Optional;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

public final class ExceptionUtils {

    private ExceptionUtils() {
    }

    public static String asShortString(Throwable t) {
        String className = t.getClass().getSimpleName();
        String message = t.getMessage();
        return StringUtils.isEmpty(message) ? className : className + ": " + message;
    }

    /**
     * Returns a {@link String} of the {@link Throwable} like:
     * <p>
     * <pre>
     * n.u.l.l.GeneralWorldFailure: You need to pray
     * -&gt; n.e.f.OrwellException: Big Brother is watching you
     * -&gt; abc.xyz.GoogleStillExistsException: null
     * </pre>
     */
    public static String asMessageList(Throwable t) {
        StringBuilder sb = new StringBuilder();
        Throwable throwable = t;
        int depth = 0;
        while (throwable != null) {
            if (depth++ > 0) {
                sb.append("\n").append("--> ");
            }
            sb.append(ClassUtils.getAbbreviatedName(throwable.getClass(), 25)).append(": ").append(throwable.getMessage());
            throwable = throwable.getCause();
        }
        return sb.toString();
    }

    /**
     * Returns <code>true</code>, if the given {@link Throwable} or one of its causes {@link Class}es is assignable from the given {@link Class}.
     */
    public static boolean isOfTypeOrHasCauseWithType(Throwable t, Class<? extends Throwable> expectedClass) {
        Throwable throwable = t;
        while (throwable != null) {
            if (expectedClass.isAssignableFrom(throwable.getClass())) {
                return true;
            }
            throwable = throwable.getCause();
        }
        return false;
    }

    /**
     * Returns a causing exception or the exception itself, if it is of given type. (If any.)
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getCauseWithType(Throwable t, Class<T> expectedClass) {
        for (Throwable throwable = t; throwable != null; throwable = throwable.getCause()) {
            if (expectedClass.isAssignableFrom(throwable.getClass())) {
                return (Optional<T>) Optional.of(throwable);
            }
        }
        return Optional.empty();
    }

}
