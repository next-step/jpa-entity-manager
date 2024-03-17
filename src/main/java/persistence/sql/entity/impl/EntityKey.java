package persistence.sql.entity.impl;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class EntityKey<T> {

    private final String name;
    private final T value;

    private EntityKey(final String name, final T value) {
        this.name = name;
        this.value = value;
    }

    public static EntityKey fromEntity(Object entity) {
        final Object value = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .map(f -> createSimpleValue(f, entity))
                .orElseThrow(IllegalArgumentException::new);

        return new EntityKey(entity.getClass().getName(), value);
    }

    public static boolean isDetachedEntity(final Object entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .anyMatch(f -> {
                    final Object value = createSimpleValue(f, entity);
                    if (f.isAnnotationPresent(GeneratedValue.class)) {

                        final GeneratedValue generatedValue = f.getAnnotation(GeneratedValue.class);
                        if (generatedValue.strategy().equals(GenerationType.IDENTITY) && isNotNull(value)) {
                            return true;
                        }
                    }

                    return false;
                });
    }

    private static boolean isNotNull(final Object value) {
        return !Objects.isNull(value);
    }

    public static <T> EntityKey fromNameAndValue(String name, T value) {
        return new EntityKey(name, value);
    }

    public T value() {
        return this.value;
    }

    private static Object createSimpleValue(Field field, Object object) {
        field.setAccessible(true);
        Object value;
        try {
            value = field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EntityKey<?> entityKey = (EntityKey<?>) o;
        return Objects.equals(name, entityKey.name) && Objects.equals(value, entityKey.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
