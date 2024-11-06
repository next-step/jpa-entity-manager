package persistence.sql.dml;

import java.lang.reflect.Field;
import java.util.Objects;
import persistence.sql.ddl.ColumnName;

public class ColumnValue {

    private final Object value;
    private final Field field;

    public ColumnValue(Field field, Object value) {
        this.value = value;
        this.field = field;
        new ValidateInsertValue(field, value);
    }

    public String toSqlValue() {
        return (value != null) ? "'" + value + "'" : "NULL";
    }

    public String toStringValue() {
        return Objects.toString(value, "");
    }

    public String getColumnNameForValue() {
        return new ColumnName(field).getColumnName();
    }

}
