package persistence.sql;

import jakarta.persistence.Id;

import java.lang.reflect.Field;

public class IdColumns extends AbstractColumns {

    public IdColumns(Class<?> clazz) {
        super(clazz);
    }

    @Override
    protected boolean addable(Field field) {
        return field.isAnnotationPresent(Id.class);
    }
}
