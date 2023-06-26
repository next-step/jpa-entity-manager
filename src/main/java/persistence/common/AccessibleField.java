package persistence.common;

import java.lang.reflect.Field;
import java.util.Objects;

public class AccessibleField {
    private final Field field;

    public AccessibleField(Field field) {
        field.setAccessible(true);
        this.field = field;
    }

    public void setValue(Object entity, Object value) {
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(Object entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessibleField field1 = (AccessibleField) o;
        return Objects.equals(field, field1.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
