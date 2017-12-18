/*
 * tksCommons
 * 
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.lang;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.thksystems.util.reflection.AnnotationUtils;

public final class ObjectUtils {

	private ObjectUtils() {
	}

	/**
	 * Using {@link HashCodeBuilder#reflectionHashCode(Object, java.util.Collection)} and ignores all fields annotated with
	 * {@link IgnoreForEquals}.
	 */
	public static int buildHashCodeReflectiveConsideringAnnotations(Object obj) {
		return HashCodeBuilder.reflectionHashCode(obj, AnnotationUtils.getFieldNamesAnnotatedWith(obj, IgnoreForEquals.class));
	}

	/**
	 * Using {@link EqualsBuilder#reflectionEquals(Object, Object, java.util.Collection)} and ignores all fields annotated with
	 * {@link IgnoreForEquals}.
	 */
	public static boolean buildEqualsReflectiveConsideringAnnotations(Object lhs, Object rhs) {
		return EqualsBuilder.reflectionEquals(lhs, rhs, AnnotationUtils.getFieldNamesAnnotatedWith(lhs, IgnoreForEquals.class));
	}

	/**
	 * Using {@link ReflectionToStringBuilder} and ignores all fields annotated with {@link IgnoreForToString}.
	 */
	public static String buildToStringReflectiveConsideringAnnotations(Object obj) {
		return ReflectionToStringBuilder.toStringExclude(obj, AnnotationUtils.getFieldNamesAnnotatedWith(obj, IgnoreForToString.class));
	}

	/**
	 * Using {@link ReflectionToStringBuilder} and ignores all fields annotated with {@link IgnoreForToString}.
	 */
	public static String buildToStringReflectiveConsideringAnnotations(Object obj, ToStringStyle style) {
		String[] ignoreList = AnnotationUtils.getFieldNamesAnnotatedWith(obj, IgnoreForToString.class);
		return new ReflectionToStringBuilder(obj, style).setExcludeFieldNames(ignoreList).toString();
	}
}
