package persistence.sql.entity.model;

import jakarta.persistence.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class DomainType {
    private static final String FORMAT = "'%s'";

    private final String name;
    private final Class<?> classType;
    private final Field field;
    private final boolean isTransient;
    private final Object value;


    public DomainType(final String name,
                      final Class<?> classType,
                      final Field field,
                      final boolean isTransient) {
        this.name = name;
        this.classType = classType;
        this.field = field;
        this.isTransient = isTransient;
        this.value = null;
    }

    public DomainType(final String name,
                      final Class<?> classType,
                      final Object value,
                      final Field field,
                      final boolean isTransient) {
        this.name = name;
        this.classType = classType;
        this.value = value;
        this.field = field;
        this.isTransient = isTransient;
    }


    public String getName() {
        return name;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public String getValue() {
        if (classType == String.class) {
            return String.format(FORMAT, value);
        }
        return value.toString();
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
                field.getName(),
                field.getType(),
                null,
                field,
                field.isAnnotationPresent(Transient.class)
        );
    }

    public static DomainType of(final Field field, final Object domain) {
        return new DomainType(
                field.getName(),
                field.getType(),
                getValue(field, domain),
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
        return name;
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
