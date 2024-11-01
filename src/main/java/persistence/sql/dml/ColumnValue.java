package persistence.sql.dml;

import java.lang.reflect.Field;
import java.util.Objects;

public class ColumnValue {

    private final Object value;

    public ColumnValue(Field field, Object value) {
        this.value = value;
        new ValidateInsertValue(field, value);
    }

    public String toSqlValue() {
        return (value != null) ? "'" + value + "'" : "NULL";
    }

    public String toStringValue() {
        return Objects.toString(value, "");
    }

}
