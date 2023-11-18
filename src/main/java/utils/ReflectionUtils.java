package utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ReflectionUtils {

    public static Object copy(Object entity) {
        try {
            Class<?> entityClass = entity.getClass();
            Object newInstance = newInstance(entityClass);

            for (Field declaredField : entityClass.getDeclaredFields()) {
                declaredField.setAccessible(true);
                declaredField.set(newInstance, declaredField.get(entity));
            }

            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException("엔터티 복사 중 오류 발생");
        }
    }

    private static Object newInstance(Class<?> entityClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> declaredConstructor = entityClass.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        return declaredConstructor.newInstance();
    }

}
