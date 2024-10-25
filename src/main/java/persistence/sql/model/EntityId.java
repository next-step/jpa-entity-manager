package persistence.sql.model;

import jakarta.persistence.Id;
import persistence.sql.exception.ExceptionMessage;
import persistence.sql.exception.RequiredIdException;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityId {

    private final Class<?> clazz;

    public EntityId(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Long getIdValue(Object object) {
        Field idField = getIdField();

        idField.setAccessible(true);
        try {
            Object fieldObject = idField.get(object);

            if (idField.get(object) == null) {
                return null;
            }

            return (Long) fieldObject;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("해당 객체에 접근할 수 없습니다.");
        }
    }

    private Field getIdField() {
        return Arrays.stream(this.clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(() -> new RequiredIdException(ExceptionMessage.REQUIRED_ID));
    }

    public Class<?> getIdType() {
        Field idField = getIdField();
        return idField.getType();
    }
}
