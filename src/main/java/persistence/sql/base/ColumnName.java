package persistence.sql.base;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import persistence.common.Fields;

import java.lang.reflect.Field;
import java.util.Objects;

public class ColumnName {
    private final Field field;
    private final Column column;

    private ColumnName(Field field, Column column) {
        this.field = field;
        this.column = column;
    }

    public static ColumnName id(Class<?> clazz) {
        Fields fields = Fields.of(clazz);
        Field idField = fields.getField(Id.class);

        return ColumnName.of(idField);
    }

    public static ColumnName of(Field field) {
        return new ColumnName(field, field.getAnnotation(Column.class));
    }

    public String name() {
        if (column == null || column.name().isBlank()) {
            return field.getName();
        }

        return column.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnName that = (ColumnName) o;
        return Objects.equals(field, that.field) && Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, column);
    }
}
