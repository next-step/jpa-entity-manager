package utils;

import persistence.sql.domain.Column;

import java.lang.reflect.Field;

public class ValueExtractor {

    private ValueExtractor() {
        throw new IllegalStateException("Utility Class Not Allowed for Construction");
    }

    public static Object extract(Object object, Column column) {
        try {
            Field field = column.getField();
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field: " + column.getName(), e);
        }
    }
}
