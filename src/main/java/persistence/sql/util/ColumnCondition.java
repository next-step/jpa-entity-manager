package persistence.sql.util;

import persistence.sql.exception.IllegalFieldAccessException;

import java.lang.reflect.Field;

public final class ColumnCondition {
    private ColumnCondition() {}

    public static String build(Field field, Object entity) {
        return new StringBuilder().
                append(ColumnName.build(field))
                .append(" = ")
                .append(getColumnValue(field, entity))
                .toString();
    }

    private static String getColumnValue(Field field, Object entity) {
        try {
            field.setAccessible(true);
            return ColumnValue.build(field.get(entity));
        } catch (IllegalAccessException e) {
            throw new IllegalFieldAccessException(e);
        }
    }
}
