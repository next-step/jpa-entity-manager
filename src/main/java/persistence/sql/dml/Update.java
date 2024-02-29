package persistence.sql.dml;

import persistence.sql.mapping.Column;
import persistence.sql.mapping.Table;

import java.util.List;
import java.util.stream.Collectors;

public class Update {

    private final Table table;

    private final List<Column> columns;

    private List<Where> whereClause = List.of();

    public Update(final Table table) {
        this.table = table;
        columns = table.getColumns();
    }

    public Update(final Table table, final List<Where> whereClause) {
        this(table);
        this.whereClause = whereClause;
    }

    public Table getTable() {
        return table;
    }

    public List<Where> getWhereClause() {
        return whereClause;
    }

    public String columnSetClause() {
        return columns.stream()
                .filter(column -> column.getValue().isNotNull())
                .map(column -> column.getName() + " = " + column.getValue().getValueClause())
                .collect(Collectors.joining(", "));
    }

}
