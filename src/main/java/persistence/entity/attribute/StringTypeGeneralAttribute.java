package persistence.entity.attribute;

import jakarta.persistence.Column;
import persistence.sql.ddl.converter.SqlConverter;

import java.lang.reflect.Field;


public class StringTypeGeneralAttribute implements GeneralAttribute {
    private final int length;
    private final String fieldName;
    private final String columnName;
    private final boolean nullable;

    public StringTypeGeneralAttribute(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);

        validate(field.getType(), column);

        this.length = column.length();
        this.fieldName = field.getName();
        this.columnName = column.name().isBlank() ? field.getName() : column.name();
        this.nullable = column.nullable();
    }

    @Override
    public String prepareDDL(SqlConverter sqlConverter) {
        String component = (columnName.isBlank() ? fieldName : columnName) + " " +
                sqlConverter.convert(String.class) +
                String.format("(%s)", length) + (!nullable ? " NOT NULL" : "");
        return component.trim();
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    private void validate(Class<?> type, Column column) {
        if (type != String.class) {
            throw new IllegalArgumentException("String 타입의 필드만 인자로 받을 수 있습니다.");
        }
        if (column == null) {
            throw new IllegalArgumentException("Column 어노테이션이 붙은 필드만 인자로 받을 수 있습니다.");
        }
    }
}
