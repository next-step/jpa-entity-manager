package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.mapping.Column;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Insert {

    private final Table table;

    private final Columns columns;

    public Insert(final Table table) {
        this.table = table;
        columns = new Columns(table.getColumns());
    }

    public Table getTable() {
        return this.table;
    }

    public List<String> getInsertableColumnNames(final Dialect dialect) {
        final List<String> insertablePkColumnNames = getInsertablePkColumns(dialect).stream()
                .map(Column::getName)
                .collect(Collectors.toList());

        final List<String> newColumns = new ArrayList<>(this.columns.getColumnNames());
        newColumns.addAll(insertablePkColumnNames);

        return newColumns;
    }

    public List<Column> getColumns() {
        return this.columns.getColumns();
    }

    public List<Column> getInsertablePkColumns(final Dialect dialect) {
        return this.columns.getPkColumnsWithValueClause(dialect);
    }

}
