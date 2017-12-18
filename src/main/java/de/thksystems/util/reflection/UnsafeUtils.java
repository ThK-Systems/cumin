package de.thksystems.util.reflection;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * Access to the internal Unsafe class.
 * <p>
 * <b>Use it only, if you really, really, really, really, really know, what you are doing.</b>
 * <p>
 * <b>It may not work with all java implementations.</b>
 */
@SuppressWarnings("restriction")
public final class UnsafeUtils {

	private UnsafeUtils() {
	}

	/**
	 * Gets the internal Unsafe class.
	 */
	public static Unsafe getUnsafe() {
		try {
			Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			return (Unsafe) unsafeField.get(null);
		} catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
			return null;
		}
	}

}
