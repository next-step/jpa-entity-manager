package persistence.entity;

import jakarta.persistence.Id;
import persistence.sql.dml.clause.FieldValue;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityMetadata {

    private final Object entity;

    public EntityMetadata(Object entity) {
        this.entity = entity;
    }

    public Long getIdValue() {
        Field field = Arrays.stream(entity.getClass()
                        .getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElse(null);

        Object idValue = new FieldValue(field).getFieldValue(entity);
        return (Long) idValue;
    }
}
