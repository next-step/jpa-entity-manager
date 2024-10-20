package persistence.sql.meta;

import jakarta.persistence.Column;

import java.lang.reflect.Field;
import java.util.Objects;

public class ColumnName {
    private final String name;

    public ColumnName(Field field) {
        this.name = value(field);
    }

    public String value() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnName that = (ColumnName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    private String value(Field field) {
        final Column column = field.getAnnotation(Column.class);
        if (Objects.nonNull(column) && Objects.nonNull(column.name()) && !column.name().isBlank()) {
            return column.name();
        }
        return field.getName();
    }
}
