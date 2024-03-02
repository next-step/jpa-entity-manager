package database.mapping.column;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.lang.reflect.Field;

public class FieldToEntityColumnConverter {

    private static final boolean DEFAULT_NULLABLE = true;
    private static final int DEFAULT_COLUMN_LENGTH = 255;

    private final Field field;

    public FieldToEntityColumnConverter(Field field) {
        this.field = field;
    }

    public EntityColumn convert() {
        Column columnAnnotation = field.getAnnotation(Column.class);
        GeneratedValue generatedValueAnnotation = field.getAnnotation(GeneratedValue.class);
        boolean isId = field.isAnnotationPresent(Id.class);

        String columnName = getColumnNameFromAnnotation(columnAnnotation, field.getName());
        Class<?> type = field.getType();
        Integer columnLength = getColumnLength(columnAnnotation);

        if (isId) {
            boolean autoIncrement = isAutoIncrement(generatedValueAnnotation);
            boolean hasIdGenerationStrategy = generatedValueAnnotation != null;
            return new PrimaryKeyEntityColumn(field, columnName, type, columnLength, autoIncrement, hasIdGenerationStrategy);
        }

        boolean nullable = isNullable(columnAnnotation);
        return new GeneralEntityColumn(field, columnName, type, columnLength, nullable);
    }

    private String getColumnNameFromAnnotation(Column columnAnnotation, String defaultName) {
        if (columnAnnotation != null && !columnAnnotation.name().isEmpty()) {
            return columnAnnotation.name();
        }
        return defaultName;
    }

    private Integer getColumnLength(Column columnAnnotation) {
        if (columnAnnotation != null) {
            return columnAnnotation.length();
        }
        return DEFAULT_COLUMN_LENGTH;
    }

    private boolean isAutoIncrement(GeneratedValue generatedValueAnnotation) {
        return generatedValueAnnotation != null && generatedValueAnnotation.strategy() == GenerationType.IDENTITY;
    }

    private boolean isNullable(Column columnAnnotation) {
        if (columnAnnotation != null) {
            return columnAnnotation.nullable();
        }
        return DEFAULT_NULLABLE;
    }
}
