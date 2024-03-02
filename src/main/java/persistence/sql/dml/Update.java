package persistence.sql.dml;

import persistence.sql.mapping.Column;
import persistence.sql.mapping.Table;

import java.util.List;
import java.util.stream.Collectors;

public class Update {

    private final Table table;

    private final Columns columns;

    private final Wheres whereClause;

    public Update(final Table table) {
        this.table = table;
        this.columns = new Columns(table.getColumns());
        final List<Where> wheres = this.columns.getPkColumns().stream()
                .map(column -> new Where(column, column.getValue(), LogicalOperator.AND, new ComparisonOperator(ComparisonOperator.Comparisons.EQ)))
                .collect(Collectors.toList());
        this.whereClause = new Wheres(wheres);
    }

    public Table getTable() {
        return table;
    }

    public String getWhereClause() {
        return this.whereClause.wheresClause();
    }

    public String columnSetClause() {
        return columns.getColumns().stream()
                .map(column -> column.getName() + " = " + column.getValue().getValueClause())
                .collect(Collectors.joining(", "));
    }

}
