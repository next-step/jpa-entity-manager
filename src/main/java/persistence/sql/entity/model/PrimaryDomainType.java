package persistence.sql.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import persistence.sql.dml.exception.InvalidFieldValueException;

import java.lang.reflect.Field;

public class PrimaryDomainType implements DomainType {

    private final Field field;
    private final GenerationType generationType;
    private final EntityColumn entityColumn;

    private PrimaryDomainType(final Field field,
                             final GenerationType generationType,
                             final EntityColumn entityColumn) {
        this.field = field;
        this.generationType = generationType;
        this.entityColumn = entityColumn;
    }

    public static PrimaryDomainType ofPrimaryDomainType(final Field field, final Object entity) {
        boolean isExistsGeneratedValue = field.isAnnotationPresent(GeneratedValue.class);
        GenerationType generationType = isExistsGeneratedValue ? field.getAnnotation(GeneratedValue.class).strategy() : GenerationType.AUTO;

        return new PrimaryDomainType(field,
                generationType,
                new EntityColumn(field.getName(), field.getType(), getField(field, entity)));
    }

    private static Object getField(final Field field, final Object entity) {
        if (entity == null) {
            return null;
        }

        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (Exception e) {
            throw new InvalidFieldValueException();
        }
    }

    public boolean isIdentityGenerationType() {
        return GenerationType.IDENTITY == generationType;
    }

    @Override
    public boolean isPrimaryDomain() {
        return true;
    }

    @Override
    public String getName() {
        return entityColumn.getName();
    }

    @Override
    public boolean isEntityColumn() {
        return true;
    }

    @Override
    public String getColumnName() {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null && !columnAnnotation.name().isEmpty()) {
            return columnAnnotation.name();
        }
        return entityColumn.getName();
    }

    @Override
    public String getValue() {
        return entityColumn.getStringValue();
    }

    @Override
    public Class<?> getClassType() {
        return entityColumn.getClassType();
    }
}
