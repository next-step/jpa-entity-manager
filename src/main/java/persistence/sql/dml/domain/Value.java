package persistence.sql.dml.domain;

import persistence.sql.ddl.domain.Column;

import java.lang.reflect.Field;
import java.util.Objects;

public class Value {

    private final Column column;
    private final Object originValue;
    private final String value;

    public Value(Column column, Object entity) {
        this.column = column;
        try {
            Field field = column.getField();
            field.setAccessible(true);
            this.originValue = field.get(entity);
            this.value = convertValue(field.getType(), field.get(entity));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Value(Column column, Class<?> valueType, Object value) {
        this.column = column;
        this.originValue = value;
        this.value = convertValue(valueType, value);
    }

    public String convertValue(Class<?> type, Object value) {
        if (type.equals(String.class) && value != null) {
            value = "'" + value + "'";
        }
        return String.valueOf(value);
    }

    public boolean isNotAutoIncrementId() {
        return column.isNotAutoIncrementId();
    }

    public boolean isNotPrimaryKeyValue() {
        return !column.isPrimaryKey();
    }

    public Column getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

    public Object getOriginValue() {
        return originValue;
    }

    public String getColumnName() {
        return column.getName();
    }

}
