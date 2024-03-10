package persistence.sql.ddl.domain;

import java.lang.reflect.Field;

public class Length {

    private static final String EMPTY_STRING = "";
    private static final String LENGTH_METHOD_NAME = "length";

    private final int length;

    public Length(Field field) {
        this.length = createColumnLength(field);
    }

    private int createColumnLength(Field field) {
        if (field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            return field.getAnnotation(jakarta.persistence.Column.class).length();
        }
        return getColumnDefaultLength();
    }

    private int getColumnDefaultLength() {
        try {
            return (int) jakarta.persistence.Column.class.getMethod(LENGTH_METHOD_NAME).getDefaultValue();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLengthString(Type type) {
        if (Type.VARCHAR.equals(type)) {
            return "(" + length + ")";
        }
        return EMPTY_STRING;
    }
}
