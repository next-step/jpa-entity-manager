package persistence.entity.attribute.id;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import persistence.sql.ddl.converter.SqlConverter;

import java.lang.reflect.Field;
import java.util.Optional;

public class IntegerTypeIdAttribute implements IdAttribute {
    private final Field field;
    private final String fieldName;
    private final String columnName;
    private final GenerationType generationType;

    public IntegerTypeIdAttribute(Field field) {
        String columnName = Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::name).orElse(field.getName());

        GenerationType generationType = Optional.ofNullable(field.getAnnotation(GeneratedValue.class))
                .map(GeneratedValue::strategy).orElse(null);

        this.field = field;
        this.fieldName = field.getName();
        this.columnName = columnName;
        this.generationType = generationType;
    }

    @Override
    public String prepareDDL(SqlConverter sqlConverter) {
        String component = (columnName.isBlank() ? fieldName : columnName) + " " +
                sqlConverter.convert(Integer.class) + " " +
                sqlConverter.convert(generationType.getClass());
        return component.trim();
    }

    @Override
    public Field getField() {
        return this.field;
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public GenerationType getGenerationType() {
        return this.generationType;
    }
}
