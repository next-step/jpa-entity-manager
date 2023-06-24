package persistence.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;

public class EntityField {
    private final Field field;

    public EntityField(Field field) {
        this.field = field;
    }

    public void setValue(Object entity, Object value) {
        field.setAccessible(true);

        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(Object entity) {
        field.setAccessible(true);

        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return field.isAnnotationPresent(annotationClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityField field1 = (EntityField) o;
        return Objects.equals(field, field1.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
