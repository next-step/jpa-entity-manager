package persistence.sql.meta.simple;

import persistence.sql.meta.Column;
import persistence.sql.meta.Columns;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class SimpleColumns implements Columns {

    private final List<SimpleColumn> columns;

    public SimpleColumns(final List<SimpleColumn> columns) {
        this.columns = columns;
    }

    @Override
    public List<String> names() {
        return this.columns.stream()
                .map(Column::getFieldName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> values() {
        return this.columns.stream()
                .map(s-> String.valueOf(s.value()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SimpleColumn> getColumns() {
        return columns;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final SimpleColumns that = (SimpleColumns) object;
        return Objects.equals(columns, that.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns);
    }

    @Override
    public String toString() {
        return "SimpleColumns{" +
                "columns=" + columns +
                '}';
    }
}
