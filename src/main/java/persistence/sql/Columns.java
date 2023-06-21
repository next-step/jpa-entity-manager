package persistence.sql;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;

public class Columns extends AbstractColumns {

    public Columns(Class<?> clazz) {
        super(clazz);
    }
    @Override
    protected boolean addable(Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

}
