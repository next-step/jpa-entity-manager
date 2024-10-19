package persistence.sql.meta;

import jakarta.persistence.Column;

import java.lang.reflect.Field;
import java.util.Objects;

public class ColumnOption {
    private final boolean isNotNull;

    public ColumnOption(Field field) {
        this.isNotNull = isNotNull(field);
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnOption that = (ColumnOption) o;
        return isNotNull == that.isNotNull;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(isNotNull);
    }

    private boolean isNotNull(Field field) {
        final Column column = field.getAnnotation(Column.class);
        if (Objects.isNull(column)) {
            return false;
        }
        return !column.nullable();
    }
}
