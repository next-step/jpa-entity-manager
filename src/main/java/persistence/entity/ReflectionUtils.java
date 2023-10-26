package persistence.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import persistence.exception.NotFoundException;

final class ReflectionUtils {

    public static <T> void setFieldValue(T instance, String fieldName, Object value) {
        try {
            final Field field = getFiled(instance.getClass(), fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getInstance(Class<T> tClass) {
        try {
            return tClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
            throw new NotFoundException(e);
        }
    }
    private static <T> Field getFiled(Class<T> tClass, String filedName) {
        try {
            return tClass.getDeclaredField(filedName);
        } catch (NoSuchFieldException e) {
            throw new NotFoundException("필드를 찾을수 없습니다.");
        }
    }

}
