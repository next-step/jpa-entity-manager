package persistence.entity;

import persistence.exception.SnapshotCreationFailedException;
import persistence.sql.exception.IllegalFieldAccessException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public final class EntityHelper {
    private EntityHelper() {}

    public static Object clone(Object object) {
        try {
            Class clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            Object cloned = clazz.getConstructor().newInstance();
            for (Field field : fields) {
                field.setAccessible(true);
                field.set(cloned, field.get(object));
            }
            return cloned;
        } catch (NoSuchMethodException
                 | InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException e) {
            throw new SnapshotCreationFailedException(e);
        }
    }

    public static boolean equals(Object object1, Object object2) {
        return Arrays.stream(object1.getClass().getDeclaredFields())
                .allMatch(field -> equals(object1, object2, field));
    }

    private static boolean equals(Object object1, Object object2, Field field) {
        try {
            field.setAccessible(true);
            return field.get(object1)
                    .equals(field.get(object2));
        } catch (IllegalAccessException e) {
            throw new IllegalFieldAccessException(e);
        }
    }
}
