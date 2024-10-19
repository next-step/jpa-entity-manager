package persistence.sql;

import jakarta.persistence.Column;
import persistence.sql.entity.EntityColumn;
import persistence.sql.entity.EntityColumns;
import persistence.sql.entity.EntityTable;

import java.lang.reflect.Field;

public class Metadata {
    private final EntityTable entityTable;
    private final EntityColumns entityColumns;


    public Metadata(Class<?> clazz) {
        this.entityTable = EntityTable.from(clazz);
        this.entityColumns = EntityColumns.from(clazz);
    }

    public String getTableName() {
        return entityTable.getTableName();
    }

    public String getIdField() {
        for (EntityColumn column : entityColumns.getColumns()) {
            if (column.isPrimaryKey()) {
                return column.getColumnName();
            }
        }
        throw new IllegalArgumentException("@Id 어노테이션이 존재하지 않음");
    }

    public String getFieldName(Field field) {
        Column annotation = field.getAnnotation(Column.class);
        if (annotation == null) {
            return field.getName();
        }
        if (!annotation.name().isEmpty()) {
            return annotation.name();
        }

        return field.getName();
    }

    public String getFieldValue(Object entity, Field field) {
        field.setAccessible(true);
        try {
            Object fieldValue = field.get(entity);
            if (fieldValue == null) {
                return "null";
            }
            return getFormattedId(fieldValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("필드에 접근할 수 없음");
        }
    }

    private String getFormattedId(Object idValue) {
        if (idValue instanceof String) {
            return String.format(("'%s'"), idValue);
        }
        return idValue.toString();
    }
}
