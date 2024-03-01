package persistence.sql.entity.model;

import jakarta.persistence.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class DomainType {
    private static final String FORMAT = "'%s'";

    private final Field field;
    private final boolean isTransient;
    private final EntityColumn entityColumn;


    public DomainType(final EntityColumn entityColumn,
                      final Field field,
                      final boolean isTransient) {
        this.entityColumn = entityColumn;
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

    public boolean isNotTransient() {
        return !isTransient;
    }

    public boolean isAnnotation(Class<? extends Annotation> annotation) {
        return field.isAnnotationPresent(annotation);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return field.getAnnotation(annotation);
    }


    public static DomainType from(Field field) {
        return new DomainType(
                new EntityColumn(field.getName(), field.getType(), null),
                field,
                field.isAnnotationPresent(Transient.class)
        );
    }

    public static DomainType of(final Field field, final Object domain) {
        return new DomainType(
                new EntityColumn(field.getName(), field.getType(), getValue(field, domain)),
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

    public String getColumnName() {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null && !columnAnnotation.name().isEmpty()) {
            return columnAnnotation.name();
        }
        return entityColumn.getName();
    }

    public boolean isExistsId() {
        return this.isAnnotation(Id.class);
    }

    public boolean isNotExistsId() {
        return !isExistsId();
    }

    public boolean isNotExistGenerateValue() {
        return !this.isAnnotation(GeneratedValue.class);
    }

    public boolean isExistsIdentity() {
        return this.getAnnotation(GeneratedValue.class).strategy() != GenerationType.IDENTITY;
    }

    public boolean isColumnAnnotation() {
        return this.isAnnotation(Column.class);
    }
}
