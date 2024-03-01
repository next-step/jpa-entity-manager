package persistence.sql.domain;

import jakarta.persistence.Column;

import java.lang.reflect.Field;
import java.util.Objects;

public class ColumnLength {

    private final Integer length;

    public ColumnLength(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && column.length() > 0) {
            this.length = column.length();
            return;
        }
        this.length = null;
    }

    public Integer getSize() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColumnLength)) return false;
        ColumnLength that = (ColumnLength) o;
        return Objects.equals(length, that.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(length);
    }
}
