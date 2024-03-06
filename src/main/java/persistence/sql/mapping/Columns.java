package persistence.sql.mapping;

import persistence.sql.dialect.Dialect;
import persistence.sql.dml.ComparisonOperator;
import persistence.sql.dml.LogicalOperator;
import persistence.sql.dml.Where;

import java.util.List;
import java.util.stream.Collectors;

public class Columns {

    private final List<Column> columns;

    private final List<Column> pkColumns;

    public Columns(final List<Column> columns) {
        this.columns = columns.stream().filter(Column::isNotPk).collect(Collectors.toList());
        this.pkColumns = columns.stream().filter(Column::isPk).collect(Collectors.toList());
    }

    public String columnSetClause() {
        return this.columns.stream()
                .map(column -> column.getName() + " = " + column.getValue().getValueClause())
                .collect(Collectors.joining(", "));
    }

    public List<Where> generatePkColumnWheres() {
        return this.pkColumns.stream()
                .map(column -> new Where(column, column.getValue(), LogicalOperator.AND, new ComparisonOperator(ComparisonOperator.Comparisons.EQ)))
                .collect(Collectors.toList());
    }

    public String columnNamesClause() {
        return this.columns.stream()
                .map(Column::getName)
                .collect(Collectors.joining(", "));
    }

    public String pkColumnNamesClause(final Dialect dialect) {
        return this.pkColumns.stream()
                .filter(column -> isPkWithValueClause(column, dialect))
                .map(Column::getName)
                .collect(Collectors.joining(", "));
    }

    public String columnValuesClause() {
        return this.columns.stream()
                .map(Column::getValue)
                .map(Value::getValueClause)
                .collect(Collectors.joining(", "));
    }

    public String pkColumnValuesClause(final Dialect dialect) {
        return this.pkColumns.stream()
                .filter(column -> isPkWithValueClause(column, dialect))
                .map(column -> getPkValueClause(column, dialect))
                .collect(Collectors.joining(", "));
    }

    private boolean isPkWithValueClause(final Column column, final Dialect dialect) {
        return !column.isIdentifierKey() || dialect.getIdentityColumnSupport().hasIdentityInsertKeyword();
    }

    private String getPkValueClause(final Column column, final Dialect dialect) {
        if (column.isIdentifierKey() && dialect.getIdentityColumnSupport().hasIdentityInsertKeyword()) {
            return dialect.getIdentityColumnSupport().getIdentityInsertString();
        }

        return column.getValue().getValueClause();
    }
}
