package persistence.entity;

import persistence.exception.SnapshotCreationFailedException;
import persistence.sql.exception.IllegalFieldAccessException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Persistable {
    @Override
    public Object clone() {
        try {
            Class clazz = getClass();
            Field[] fields = clazz.getDeclaredFields();
            Object cloned = clazz.getConstructor().newInstance();
            for (Field field : fields) {
                field.setAccessible(true);
                field.set(cloned, field.get(this));
            }
            return cloned;
        } catch (NoSuchMethodException
                 | InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException e) {
            throw new SnapshotCreationFailedException(e);
        }
    }

    @Override
    public boolean equals(Object object) {
        return Arrays.stream(getClass().getDeclaredFields())
                .allMatch(field -> equals(object, field));
    }

    private boolean equals(Object object, Field field) {
        try {
            field.setAccessible(true);
            return field.get(object)
                    .equals(field.get(this));
        } catch (IllegalAccessException e) {
            throw new IllegalFieldAccessException(e);
        }
    }
}
