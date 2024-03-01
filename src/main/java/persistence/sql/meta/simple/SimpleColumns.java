package persistence.sql.meta.simple;

import persistence.sql.meta.Column;
import persistence.sql.meta.Columns;

import java.util.List;
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
    public List<String> values(Object object) {
        return this.columns.stream()
                .map(c -> c.value(object))
                .collect(Collectors.toList());
    }

    @Override
    public List<SimpleColumn> getColumns() {
        return columns;
    }
}
