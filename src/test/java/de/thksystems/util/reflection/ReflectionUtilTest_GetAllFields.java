package de.thksystems.util.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

class GAF_HyperSuperClass {
    Boolean areYouAllRight = null;
}

class GAF_IntermedClass extends GAF_HyperSuperClass {
}

@SuppressWarnings("unused")
class GAF_SuperClass extends GAF_IntermedClass {
    public static final String CONST = "CONST";
    private List<String> superList;
}

class GAF_BogusClass extends GAF_SuperClass {
    String bogus;
}

@SuppressWarnings("unused")
class GAF_MyClass extends GAF_SuperClass {
    public GAF_SuperClass bar;
    protected transient BigDecimal transi;
    private long foo;
}

@SuppressWarnings("unused")
class GAF_SubClass extends GAF_MyClass {
    private final int sub = 0;
}

public class ReflectionUtilTest_GetAllFields {

    @Test
    public void testGetAllFields() {
        List<Field> allFields = ReflectionUtils.getAllFields(GAF_MyClass.class);
        assertEquals(6, allFields.size());
        for (Field field : allFields) {
            assertNotEquals("bogus", field.getName());
            assertNotEquals("sub", field.getName());
        }
    }
}
