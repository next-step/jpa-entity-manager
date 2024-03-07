package persistence.sql.dml;

import persistence.sql.mapping.Column;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.Table;

import java.util.List;
import java.util.stream.Collectors;

public class Update {

    private final Table table;

    private final Columns columns;

    private final Wheres wheres;

    public Update(final Table table) {
        this.table = table;
        this.columns = new Columns(table.getColumns());
        final List<Where> wheres = this.columns.getPkColumns().stream()
                .map(column -> new Where(column, column.getValue(), LogicalOperator.AND, new ComparisonOperator(ComparisonOperator.Comparisons.EQ)))
                .collect(Collectors.toList());
        this.wheres = new Wheres(wheres);
    }

    public Table getTable() {
        return table;
    }

    public List<Column> getColumns() {
        return this.columns.getColumns();
    }

    public List<Where> getWheres() {
        return this.wheres.getWheres();
    }

}
