package persistence.entity.attribute.id;

import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import persistence.sql.ddl.converter.SqlConverter;

import java.lang.reflect.Field;
import java.util.Optional;

public class StringTypeIdAttribute implements IdAttribute {
    private final Field field;
    private final String fieldName;
    private final Integer length;
    private final String columnName;
    private final GenerationType generateValueStrategy;

    public StringTypeIdAttribute(Field field) {
        Optional<Column> columnOptional = Optional.ofNullable(field.getAnnotation(Column.class));

        validate(field.getType());

        this.field = field;
        this.fieldName = field.getName();
        this.length = columnOptional.map(Column::length).orElse(255);
        this.columnName = columnOptional.map(Column::name).orElse(field.getName());
        this.generateValueStrategy = null;
    }

    @Override
    public String prepareDDL(SqlConverter sqlConverter) {
        String component = (columnName.isBlank() ? fieldName : columnName) +
                " " + sqlConverter.convert(String.class) +
                String.format("(%s)", length) +
                " ";
        return component.trim();
    }

    @Override
    public Field getField() {
        return field;
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
        return this.generateValueStrategy;
    }

    private void validate(Class<?> type) {
        if (type != String.class) {
            throw new IllegalArgumentException("String 타입의 필드만 인자로 받을 수 있습니다.");
        }
    }
}
