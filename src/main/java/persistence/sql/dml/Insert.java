package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.Table;

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

    public String columnNameClause(final Dialect dialect) {
        final String columnNameClause = this.columns.columnNamesClause();

        final StringBuilder clause = new StringBuilder()
                .append(columnNameClause);

        if (clause.length() > 0) {
            clause.append(", ");
        }

        final String pkColumnNameClause = this.columns.pkColumnNamesClause(dialect);

        clause.append(pkColumnNameClause);

        return clause.toString();
    }

    public String columnValueClause(final Dialect dialect) {
        final String columnNameClause = this.columns.columnValuesClause();

        final StringBuilder clause = new StringBuilder()
                .append(columnNameClause);

        if (clause.length() > 0) {
            clause.append(", ");
        }

        final String pkColumnNameClause = this.columns.pkColumnValuesClause(dialect);

        clause.append(pkColumnNameClause);

        return clause.toString();
    }

}
