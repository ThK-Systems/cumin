/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

public final class AnnotationUtils {

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
        // @formatter:off
        return ReflectionUtils.getAllFields(obj.getClass()).stream()
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .toArray(Field[]::new);
        // @formatter:on
    }

}
