package persistence.entity;

import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import persistence.exception.NotExistException;

public record EntityId(Object id) {

    public EntityId(Object entity, Class<?> entityType) {
        this(getIdField(entity, entityType));
    }

    private static Object getIdField(Object entity, Class<?> entityType) {
        Field[] fields = entityType.getDeclaredFields();
        Field idField = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new NotExistException("identification."));

        try {
            return idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
