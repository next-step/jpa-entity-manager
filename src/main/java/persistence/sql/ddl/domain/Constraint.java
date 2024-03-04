package persistence.sql.ddl.domain;

import java.lang.reflect.Field;

public class Constraint {

    private final PrimaryKey primaryKey;
    private final boolean isNotNull;

    public Constraint(Field field) {
        this.primaryKey = new PrimaryKey(field);
        this.isNotNull = isNotNull(field);
    }

    private boolean isNotNull(Field field) {
        return field.isAnnotationPresent(jakarta.persistence.Column.class) && !field.getAnnotation(jakarta.persistence.Column.class).nullable();
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public boolean isPrimaryKey() {
        return primaryKey.isPrimaryKey();
    }

    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

}
