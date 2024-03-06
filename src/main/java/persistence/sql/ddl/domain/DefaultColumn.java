package persistence.sql.ddl.domain;

import jakarta.persistence.GenerationType;

import java.lang.reflect.Field;

public class DefaultColumn implements Column {

    private final Field field;

    private final String name;

    private final Type type;

    private final Length length;

    private final Constraint constraint;

    public DefaultColumn(Field field) {
        this.field = field;
        this.name = createColumnName(field);
        this.type = createColumnType(field);
        this.length = new Length(field);
        this.constraint = new Constraint(field);
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

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLength() {
        return length.getLengthString(type);
    }

    @Override
    public boolean isNotNull() {
        return constraint.isNotNull();
    }

    @Override
    public boolean isPrimaryKey() {
        return false;
    }

    @Override
    public GenerationType getGenerationType() {
        return null;
    }

    @Override
    public boolean isNotAutoIncrementId() {
        return true;
    }

}
