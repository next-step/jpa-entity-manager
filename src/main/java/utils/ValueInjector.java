package utils;

import persistence.sql.domain.Column;

import java.lang.reflect.Field;

public class ValueInjector {

    private ValueInjector() {
        throw new IllegalStateException("Utility Class Not Allowed for Construction");
    }

    public static void inject(Object object, Column column, Object injectValue) {
        try {
            Field field = column.getField();
            field.setAccessible(true);
            field.set(object, injectValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field: " + column.getName(), e);
        }
    }
}
