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

    public EntityColumnName getIdColumnName() {
        Field idField = getIdField();
        return new EntityColumnName(idField);
    }

    public EntityColumnValue getIdValue(Object object) {
        Field idField = getIdField();
        return new EntityColumnValue(idField, object);
    }

    private Field getIdField() {
        return Arrays.stream(this.clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(() -> new RequiredIdException(ExceptionMessage.REQUIRED_ID));
    }
}
