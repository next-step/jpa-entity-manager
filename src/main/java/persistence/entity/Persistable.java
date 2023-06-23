package persistence.entity;

import persistence.exception.SnapshotCreationFailedException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

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
}
