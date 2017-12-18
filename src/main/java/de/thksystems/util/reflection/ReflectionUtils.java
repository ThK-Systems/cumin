/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reflection-Helper.
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Gets all fields from a class including the fields from all its super-classes.
     */
    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fieldList = new ArrayList<>();
        getAllFields(fieldList, type);
        return fieldList;
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    /**
     * Get non-null field values of the given objects filtered by field type.
     *
     * @param deepIntoCollectionAndArrays Add field values from collections and arrays (1-dim), if matching given field type.
     * @param excludeTransient            Exclude transients fields
     */
    public static <T> Set<T> getFieldValuesOfType(Object object, Class<T> expectedFieldType, boolean deepIntoCollectionAndArrays, boolean excludeTransient)
            throws IllegalArgumentException, IllegalAccessException {
        Set<T> valueSet = new HashSet<>();
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            // Exclude transient fields, if set
            if (excludeTransient && Modifier.isTransient(declaredField.getModifiers())) {
                continue;
            }
            // Set and remember field accessibility
            boolean declaredFieldAccessibility = declaredField.isAccessible();
            declaredField.setAccessible(true);
            // Check for field type
            Class<?> declaredFieldType = declaredField.getDeclaringClass();
            // ... field is of expected type
            if (expectedFieldType.isAssignableFrom(declaredFieldType)) {
                @SuppressWarnings("unchecked")
                T declaredFieldValue = (T) declaredField.get(object);
                if (declaredFieldValue != null) {
                    valueSet.add(declaredFieldValue);
                }
            }
            // ... field is of collection type
            if (deepIntoCollectionAndArrays && Collection.class.isAssignableFrom(declaredFieldType)) {
                @SuppressWarnings("rawtypes")
                Collection col = (Collection) declaredField.get(object);
                for (Object colEntry : col) {
                    if (colEntry != null && expectedFieldType.isAssignableFrom(colEntry.getClass())) {
                        @SuppressWarnings("unchecked")
                        T value = (T) colEntry;
                        valueSet.add(value);
                    }
                }
            }
            // ... field is array
            if (deepIntoCollectionAndArrays && declaredFieldType.isArray()) {
                Object[] array = (Object[]) declaredField.get(object);
                for (Object arrayEntry : array) {
                    if (arrayEntry != null && expectedFieldType.isAssignableFrom(arrayEntry.getClass())) {
                        @SuppressWarnings("unchecked")
                        T value = (T) arrayEntry;
                        valueSet.add(value);
                    }
                }
            }
            // Reset field accessibility
            declaredField.setAccessible(declaredFieldAccessibility);
        }
        return valueSet;
    }

    /**
     * Returns the field value, even of a private field.
     */
    public static Object getFieldValue(Object myObject, Field field) throws IllegalArgumentException, IllegalAccessException {
        boolean oldAccessibility = field.isAccessible();
        field.setAccessible(true);
        Object fieldValue = field.get(myObject);
        field.setAccessible(oldAccessibility);
        return fieldValue;
    }

}
