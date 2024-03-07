package persistence.sql.mapping;

import persistence.sql.dialect.Dialect;

import java.util.Collections;
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
        return Collections.unmodifiableList(this.columns);
    }

    public List<Column> getPkColumns() {
        return Collections.unmodifiableList(this.pkColumns);
    }

    public List<String> getColumnNames() {
        return this.columns.stream()
                .map(Column::getName)
                .collect(Collectors.toList());
    }

    public List<String> getPkColumnNamesWithValueClause(final Dialect dialect) {
        return this.getPkColumnsWithValueClause(dialect).stream()
                .map(Column::getName)
                .collect(Collectors.toList());
    }

    public List<Column> getPkColumnsWithValueClause(final Dialect dialect) {
        return this.pkColumns.stream()
                .filter(column -> isPkWithValueClause(column, dialect))
                .collect(Collectors.toList());
    }

    private boolean isPkWithValueClause(final Column column, final Dialect dialect) {
        return !column.isIdentifierKey() || dialect.getIdentityColumnSupport().hasIdentityInsertKeyword();
    }
}
