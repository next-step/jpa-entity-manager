package persistence.sql.metadata;

import jakarta.persistence.Transient;
import persistence.dialect.Dialect;

import java.lang.reflect.Field;

public class Column {
    private final String name;

    private final Class<?> type;

    private final Constraint constraint;

    private final boolean isTransient;

    private final String convertedValue;

    private final Object value;

    public Column(Field field, Object value) {
        this.name = findName(field);
        this.type = field.getType();
        this.constraint = new Constraint(field);
        this.isTransient = field.isAnnotationPresent(Transient.class);
        this.convertedValue = convertValueToString(value);
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public boolean isNotTransient() {
        return !isTransient;
    }

    public String getConvertedValue() {
        return convertedValue;
    }

    public Object getValue() {
        return value;
    }

    public String buildColumnsWithConstraint(Dialect dialect) {
        return new StringBuilder()
                .append(name + " " + findType(dialect))
                .append(constraint.buildNullable())
                .append(dialect.getGeneratedStrategy(constraint.getGeneratedType()))
                .append(constraint.buildPrimaryKey())
                .toString();
    }

    public boolean checkPossibleToBeValue() {
        if("null".equals(value) && isNotNullable()) {
            return false;
        }

        return !isTransient && !constraint.isPrimaryKey();
    }

    public boolean isPrimaryKey() {
        return constraint.isPrimaryKey();
    }

    public boolean isNotNullable() {
        return !constraint.isNullable();
    }

    private String findName(Field field) {
        if(!field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            return field.getName();
        }

        jakarta.persistence.Column column = field.getDeclaredAnnotation(jakarta.persistence.Column.class);

        if("".equals(column.name())) {
            return field.getName();
        }

        return column.name();
    }

    private String findType(Dialect dialect) {
        return dialect.getColumnType(type);
    }

    private String convertValueToString(Object value) {
        if(type.equals(String.class) && value != null) {
            value = "'" + value + "'";
        }

        return String.valueOf(value);
    }
}
