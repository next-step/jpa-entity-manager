package persistence.entity;

import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import persistence.exception.NotExistException;

public class EntityId {

    private EntityId() {

    }

    public static Object getIdValue(Object entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
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
