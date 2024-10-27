package persistence.entity;

import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import persistence.exception.NotExistException;

public record EntityKey(Object key, Class<?> entityType) {

    public EntityKey(Object entity) {
        this(getKey(entity), entity.getClass());
    }

    private static Object getKey(Object entity) {
        Field idField = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new NotExistException("identification."));
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
        if (!(o instanceof EntityKey)) {
            return false;
        }
        EntityKey entityKey = (EntityKey) o;
        return Objects.equals(key, entityKey.key) && Objects.equals(entityType, entityKey.entityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, entityType);
    }
}
