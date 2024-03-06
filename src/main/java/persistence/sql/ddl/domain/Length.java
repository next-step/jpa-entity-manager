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
        return 255;
    }

    public String getLengthString(Type type) {
        if (Type.VARCHAR.equals(type)) {
            return "(" + length + ")";
        }
        return EMPTY_STRING;
    }
}
