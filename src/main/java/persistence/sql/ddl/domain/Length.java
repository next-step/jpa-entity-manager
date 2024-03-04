package persistence.sql.ddl.domain;

import java.lang.reflect.Field;

public class Length {

    private static final String EMPTY_STRING = "";

    private final int length;

    public Length(Field field) {
        this.length = createColumnLength(field);
    }

    private int createColumnLength(Field field) {
        if (field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            return field.getAnnotation(jakarta.persistence.Column.class).length();
        }
        return 0;
    }

    public String getLengthString() {
        if (length == 0) {
            return EMPTY_STRING;
        }
        return "(" + length + ")";
    }
}
