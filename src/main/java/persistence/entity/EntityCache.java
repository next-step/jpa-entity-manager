package persistence.entity;

import common.ErrorCode;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public record EntityCache(Object key, Class<?> entityType) {
    public EntityCache(Object entity) {
        this(getKey(entity), entity.getClass());
    }

    private static Object getKey(Object entity) {
        Field idField = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_EXIST_ID_ANNOTATION.getErrorMsg()));
        try {
            idField.setAccessible(true);
            return idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityCache entityCache)) {
            return false;
        }
        return Objects.equals(key, entityCache.key) && Objects.equals(entityType, entityCache.entityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, entityType);
    }
}
