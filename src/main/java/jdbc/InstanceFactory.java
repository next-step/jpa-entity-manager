package jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class InstanceFactory<T> {
    public static final String NO_DEFAULT_CONSTRUCTOR_FAILED_MESSAGE = "기본 생성자가 없습니다.";
    private static final String INSTANCE_CREATION_FAILED_MESSAGE = "인스턴스 생성을 실패하였습니다.";

    private final Class<T> clazz;

    public InstanceFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T createInstance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(NO_DEFAULT_CONSTRUCTOR_FAILED_MESSAGE);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(INSTANCE_CREATION_FAILED_MESSAGE);
        }
    }

    public T copy(Object instance) {
        try {
            final T newInstance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                mapField(field, instance, newInstance);
            }
            return newInstance;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(NO_DEFAULT_CONSTRUCTOR_FAILED_MESSAGE);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(INSTANCE_CREATION_FAILED_MESSAGE);
        }
    }

    private void mapField(Field field, Object instance, T newInstance) {
        try {
            field.setAccessible(true);
            final Object value = field.get(instance);
            field.set(newInstance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
