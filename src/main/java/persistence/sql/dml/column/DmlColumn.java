package persistence.sql.dml.column;

import jakarta.persistence.Id;
import persistence.common.Fields;
import persistence.sql.base.ColumnName;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class DmlColumn {
    private final ColumnName columnName;
    private final ColumnValue columnValue;

    public DmlColumn(Field field, Object entity) {
        this(getColumnName(field), getColumnValue(field, entity));
    }

    public DmlColumn(ColumnName columnName, ColumnValue columnValue) {
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    public static DmlColumn id(Class<?> clazz, Object entity) {
        Fields fields = Fields.of(clazz);
        Field idField = fields.getField(Id.class);

        return new DmlColumn(idField, entity);
    }

    private static ColumnName getColumnName(Field field) {
        return ColumnName.of(field);
    }

    private static ColumnValue getColumnValue(Field field, Object entity) {
        field.setAccessible(true);

        try {
            return new ColumnValue(field.get(entity));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String name() {
        return columnName.name();
    }

    public String value() {
        return columnValue.value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DmlColumn dmlColumn = (DmlColumn) o;
        return Objects.equals(columnName, dmlColumn.columnName) && Objects.equals(columnValue, dmlColumn.columnValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, columnValue);
    }
}
