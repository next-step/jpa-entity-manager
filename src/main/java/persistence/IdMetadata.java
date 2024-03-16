package persistence;

import jakarta.persistence.Id;
import persistence.sql.dml.clause.FieldValue;

import java.lang.reflect.Field;
import java.util.Arrays;

public class IdMetadata {

    private final Object entity;

    public IdMetadata(Object entity) {
        this.entity = entity;
    }

    public Long getMetadata() {
        Field field = Arrays.stream(entity.getClass()
                        .getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElse(null);

        Object idValue = new FieldValue(field).getFieldValue(entity);
        return (Long) idValue;
    }
}
