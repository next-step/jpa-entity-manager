package persistence.sql.dml.h2;

import jakarta.persistence.Id;
import persistence.sql.exception.EntityIdNotFoundException;
import persistence.sql.exception.IllegalFieldAccessException;
import persistence.sql.util.ColumnValue;

import java.lang.reflect.Field;
import java.util.Arrays;

public final class H2WhereIdQuery {
    private H2WhereIdQuery() {}

    public static String build(Object entity) {
        final Class<?> clazz = entity.getClass();
        try {
            final Object id = getIdField(clazz).get(entity);
            return build(clazz, id);
        } catch (IllegalAccessException e) {
            throw new IllegalFieldAccessException(e);
        }
    }

    public static String build(Class<?> clazz, Object id) {
        final Field idField = getIdField(clazz);
        return new StringBuilder()
                .append(" WHERE ")
                .append(idField.getName())
                .append(" = ")
                .append(ColumnValue.build(id))
                .toString();
    }

    private static Field getIdField(Class<?> clazz) {
        final Field idField = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .orElseThrow(() -> new EntityIdNotFoundException());
        idField.setAccessible(true);
        return idField;
    }
}
