/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AnnotationUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationUtils.class);

    private AnnotationUtils() {
    }

    /**
     * Gets the names of all fields annotated with the given annotation (including super classes).
     */
    public static String[] getFieldNamesAnnotatedWith(Object obj, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(getFieldsAnnotatedWith(obj, annotationClass)).map(Field::getName).toArray(String[]::new);
    }

    /**
     * Gets all fields annotated with the given annotation (including super classes)..
     */
    public static Field[] getFieldsAnnotatedWith(Object obj, Class<? extends Annotation> annotationClass) {
        return FieldUtils.getAllFieldsList(obj.getClass()).stream()
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .toArray(Field[]::new);
    }

    public static Collection<String> getPropertyNamesListWithAnnotation(Class<?> targetClass, Class<? extends Annotation> annotationClass) {
        Set<String> fieldNamesWithAnnotation = FieldUtils.getFieldsListWithAnnotation(targetClass, annotationClass).stream().map(Field::getName).collect(Collectors.toSet());
        fieldNamesWithAnnotation.addAll(MethodUtils.getMethodsListWithAnnotation(targetClass, annotationClass, true, false).stream()
                .map(Method::getName)
                .filter(AnnotationUtils::isValidGetterOrSetter)
                .map(name -> StringUtils.uncapitalize(RegExUtils.replaceFirst(name, "^(get|set|is)", "")))
                .collect(Collectors.toSet()));
        return fieldNamesWithAnnotation;
    }

    private static boolean isValidGetterOrSetter(String methodName) {
        if (!StringUtils.startsWithAny(methodName, "get", "set", "is")) {
            LOG.warn("Annotated method is no valid getter or setter: '{}' -> Ignoring", methodName);
            return false;
        }
        return true;
    }

    public static String[] getPropertyNamesWithAnnotation(Class<?> targetClass, Class<? extends Annotation> annotationClass) {
        return getPropertyNamesListWithAnnotation(targetClass, annotationClass).toArray(new String[0]);
    }

}
