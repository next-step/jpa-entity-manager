package persistence.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.NotNull;

public class EntityCopyUtils {

    public static Object deepCopy(Object entity) {
        Class<?> clazz = entity.getClass();
        Object copyEntity = getNewInstance(clazz);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = getValue(entity, field);
            setField(copyEntity, field, value);
        }

        return copyEntity;
    }

    private static void setField(Object copyEntity, Field field, Object value) {
        try {
            field.set(copyEntity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getValue(Object entity, Field field) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static Object getNewInstance(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
