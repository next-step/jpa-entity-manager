package persistence.sql.dml.clause;

import java.lang.reflect.Field;

public class FieldValue {

    private final Field field;

    public FieldValue(Field field) {
        this.field = field;
    }

    public Object getFieldValue(Object entity) {
        field.setAccessible(true);

        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("not access %s field", field.getName()));
        }
    }
}
