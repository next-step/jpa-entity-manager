package persistence.sql.mapping;

import persistence.sql.mapping.Column;

import java.util.List;
import java.util.stream.Collectors;

public class Columns {

    private final List<Column> columns;

    private final List<Column> pkColumns;

    public Columns(final List<Column> columns) {
        this.columns = columns.stream().filter(Column::isNotPk).collect(Collectors.toList());
        this.pkColumns = columns.stream().filter(Column::isPk).collect(Collectors.toList());
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    public List<Column> getPkColumns() {
        return this.pkColumns;
    }
}
