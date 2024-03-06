package persistence.sql.ddl.domain;

import java.lang.reflect.Field;

public class Constraint {

    private final boolean isNotNull;

    public Constraint(Field field) {
        this.isNotNull = isNotNull(field);
    }

    private boolean isNotNull(Field field) {
        return field.isAnnotationPresent(jakarta.persistence.Column.class) && !field.getAnnotation(jakarta.persistence.Column.class).nullable();
    }

    public boolean isNotNull() {
        return isNotNull;
    }

}
