package persistence.sql;

import java.lang.reflect.Field;
import persistence.exception.ReflectionRuntimeException;

public class EntityColumn {
    private final Field field;

    public EntityColumn(Field field) {
        this.field = field;
    }

    public String getColumnName() {
        if (!field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            return field.getName();
        }

        jakarta.persistence.Column column = field.getAnnotation(jakarta.persistence.Column.class);

        if (column.name().isEmpty()) {
            return field.getName();
        }

        return column.name();
    }

    public EntityColumnValue getEntityColumnValueFrom(Object entity) throws ReflectionRuntimeException {
        try {
            field.setAccessible(true);

            return new EntityColumnValue(field.get(entity));
        } catch (IllegalAccessException e) {
            throw new ReflectionRuntimeException(entity, e);
        }
    }

    public boolean isPrimary() {
        return field.isAnnotationPresent(jakarta.persistence.Id.class);
    }
}
