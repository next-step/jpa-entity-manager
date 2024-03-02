package persistence.sql.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;

public class NormalDomainType implements DomainType {

    private final Field field;
    private final Column column;
    private final boolean isTransient;
    private final EntityColumn entityColumn;


    private NormalDomainType(final EntityColumn entityColumn,
                             final Column column,
                             final Field field,
                             final boolean isTransient) {
        this.entityColumn = entityColumn;
        this.column = column;
        this.field = field;
        this.isTransient = isTransient;
    }

    public String getName() {
        return entityColumn.getName();
    }

    public Class<?> getClassType() {
        return entityColumn.getClassType();
    }

    public String getValue() {
        return entityColumn.getStringValue();
    }

    public boolean isNotNull() {
        if (column == null) {
            return false;
        }

        return !column.nullable();
    }

    public static NormalDomainType of(final Field field, final Object domain) {
        Column column = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class) : null;

        return new NormalDomainType(
                new EntityColumn(field.getName(), field.getType(), getValue(field, domain)),
                column,
                field,
                field.isAnnotationPresent(Transient.class)
        );
    }

    private static Object getValue(final Field field,
                                   final Object domain) {
        try {
            field.setAccessible(true);
            return field.get(domain);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isPrimaryDomain() {
        return false;
    }

    @Override
    public boolean isEntityColumn() {
        return !isTransient;
    }

    @Override
    public String getColumnName() {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null && !columnAnnotation.name().isEmpty()) {
            return columnAnnotation.name();
        }
        return entityColumn.getName();
    }
}
