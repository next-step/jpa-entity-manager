package persistence.sql.meta.parser;

import java.lang.reflect.Field;

public class ValueParser {
    private ValueParser() {
    }

    public static String valueParse(Field field, Object object) {
        field.setAccessible(true);
        Object value;
        try {
            value = field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (field.getType().equals(String.class)) {
            return String.format("'%s'", value);
        }

        if (field.getType().equals(Long.class)) {
            return String.format("%dL", value);
        }

        return String.valueOf(value);
    }
}
