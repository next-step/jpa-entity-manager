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

}
