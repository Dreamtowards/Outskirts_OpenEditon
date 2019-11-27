package outskirts.util;

import java.lang.reflect.Field;
import java.util.function.Predicate;

/**
 * ReflectionUtils looks more Powerful than ReflectUtils and amateur than Reflects
 */
public class ReflectionUtils {

    public static Field getField(Class<?> sourceClass, String fieldname, boolean findUpward, Predicate<Class<?>> predicate) {
        Field field = null;
        for (Class clazz = sourceClass;clazz != Object.class;clazz = clazz.getSuperclass()) {
            if (predicate == null || predicate.test(clazz)) {
                try {
                    field = clazz.getDeclaredField(fieldname);
                } catch (NoSuchFieldException ex) { }
            }
            if (!findUpward || field != null) {
                break;
            }
        }
        if (field != null) {
            field.setAccessible(true);
        }
        return field;
    }

    public static Field getField(Class<?> sourceClass, String fieldname) {
        return getField(sourceClass, fieldname, false, null);
    }

    public static <T> T getFieldValue(Field field, Object owner) {
        try {
            field.setAccessible(true);
            return (T) field.get(owner);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Failed to get field value.", ex);
        }
    }

    public static void setFieldValue(Field field, Object owner, Object value) {
        try {
            field.setAccessible(true);
            field.set(owner, value);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Failed to set field value.", ex);
        }
    }

}
