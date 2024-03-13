package persistence.sql.ddl.domain;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.lang.reflect.Field;

public interface Column {

    static Column from(Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            return new PkColumn(field);
        }
        return new DefaultColumn(field);
    }

    default void setFieldValue(Object entity, Object value) {
        try {
            getField().setAccessible(true);
            getField().set(entity, value);
            getField().setAccessible(false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    Field getField();

    String getName();

    Type getType();

    String getLength();

    boolean isNotNull();

    boolean isPrimaryKey();

    GenerationType getGenerationType();

    boolean isNotAutoIncrementId();

}
