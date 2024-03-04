package persistence.sql.ddl.domain;

import java.lang.reflect.Field;

public class Column {

    private final Field field;

    private final String name;

    private final Type type;

    private final int length;

    private final Constraint constraint;

    public Column(Field field) {
        this.field = field;
        this.name = createColumnName(field);
        this.type = createColumnType(field);
        this.length = createColumnLength(field);
        this.constraint = new Constraint(field);
    }

    private int createColumnLength(Field field) {
        if (field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            return field.getAnnotation(jakarta.persistence.Column.class).length();
        }
        return 0;
    }

    public String createColumnName(Field field) {
        if (field.isAnnotationPresent(jakarta.persistence.Column.class) && !field.getAnnotation(jakarta.persistence.Column.class).name().isBlank()) {
            return field.getAnnotation(jakarta.persistence.Column.class).name();
        }

        return field.getName();
    }

    public Type createColumnType(Field field) {
        return Type.of(field.getType());
    }

    public Field getField() {
        return field;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public boolean isPrimaryKey() {
        return constraint.isPrimaryKey();
    }

    private boolean isNotNull() {
        return constraint.isNotNull();
    }
}
