package persistence.sql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import persistence.sql.domain.FieldType;

import java.lang.reflect.Field;

public class EntityColumn {

    private final String columnName;
    private final FieldType fieldType;
    private final boolean isPrimaryKey;
    private final boolean isNullable;

    public EntityColumn(String columnName, FieldType fieldType, boolean isPrimaryKey, boolean isNullable) {
        this.columnName = columnName;
        this.fieldType = fieldType;
        this.isPrimaryKey = isPrimaryKey;
        this.isNullable = isNullable;
    }

    public static EntityColumn from(Field field) {
        return new EntityColumn(
                getFieldName(field),
                FieldType.from(field.getType()),
                field.isAnnotationPresent(Id.class),
                isColumnNullable(field)
        );
    }

    private static boolean isColumnNullable(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            return false;
        }
        Column annotation = field.getAnnotation(Column.class);
        return !annotation.nullable();
    }

    public String getColumnName() {
        return columnName;
    }

    public static String getFieldName(Field field) {
        Column annotation = field.getAnnotation(Column.class);
        if (annotation == null) {
            return field.getName();
        }
        if (!annotation.name().isEmpty()) {
            return annotation.name();
        }

        return field.getName();
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isNullable() {
        return isNullable;
    }
}

