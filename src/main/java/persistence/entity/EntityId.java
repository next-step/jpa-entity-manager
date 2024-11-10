package persistence.entity;

import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import persistence.exception.NotExistException;

public record EntityId(Object id) {

    public EntityId(Object entity, Class<?> entityType) {
        this(getIdValue(entity, entityType));
    }

    private static Object getIdValue(Object entity, Class<?> entityType) {
        Field[] fields = entityType.getDeclaredFields();
        Field idField = Arrays.stream(fields)
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

    public static Field getIdField(Object entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        return Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new NotExistException("identification."));
    }

}
