package md.utm.pad.labs.node.dsl.util;

import java.lang.reflect.Field;

/**
 * Created by anrosca on Nov, 2017
 */
public class ReflectionUtil {

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getFieldValue(Object target, Field field) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
           throw new RuntimeException(e);
        }
    }
}
