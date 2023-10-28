package persistence.entity.attribute;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import persistence.sql.ddl.converter.SqlConverter;

import java.lang.reflect.Field;

public class IntegerTypeGeneralAttribute implements GeneralAttribute {
    private final boolean nullable;
    private final int scale;
    private final String fieldName;
    private final String columnName;

    public IntegerTypeGeneralAttribute(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);

        assert column != null;

        this.scale = column.scale();
        this.fieldName = field.getName();
        this.columnName = column.name().isBlank() ? field.getName() : column.name();
        this.nullable = field.isAnnotationPresent(Id.class);
    }

    @Override
    public String prepareDDL(SqlConverter sqlConverter) {
        StringBuilder component = new StringBuilder();

        component.append(columnName.isBlank() ? fieldName : columnName);
        component.append(" ").append(sqlConverter.convert(Integer.class));
        if (scale != 0) {
            component.append(String.format(" (%s)", scale));
        }
        if (!nullable) {
            component.append(" NOT NULL");
        }
        return component.toString().trim();
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
