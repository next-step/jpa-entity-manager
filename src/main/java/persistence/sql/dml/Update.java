package persistence.sql.dml;

import persistence.sql.mapping.Column;
import persistence.sql.mapping.Table;

import java.util.List;
import java.util.stream.Collectors;

public class Update {

    private final Table table;

    private final List<Column> columns;

    private List<Where> whereClause;

    public Update(final Table table) {
        this.table = table;
        this.columns = table.getColumns();
        this.whereClause = this.columns.stream()
                .filter(Column::isPk)
                .map(column -> new Where(column, column.getValue(), LogicalOperator.AND, new ComparisonOperator(ComparisonOperator.Comparisons.EQ)))
                .collect(Collectors.toList());
        this.whereClause.stream().findFirst().ifPresent(where -> where.changeLogicalOperator(LogicalOperator.NONE));
    }

    public Table getTable() {
        return table;
    }

    public List<Where> getWhereClause() {
        return whereClause;
    }

    public String columnSetClause() {
        return columns.stream()
                .filter(Column::isNotPk)
                .map(column -> column.getName() + " = " + column.getValue().getValueClause())
                .collect(Collectors.joining(", "));
    }

}
