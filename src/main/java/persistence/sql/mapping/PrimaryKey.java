package persistence.sql.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PrimaryKey {

    private final List<Column> columns = new ArrayList<>();

    public PrimaryKey(final Column... columns) {
        this.columns.addAll(List.of(columns));
    }

    public String sqlConstraintString() {
        final StringBuilder statement = new StringBuilder("primary key (");

        final String columnsStatement = columns.stream().map(Column::getName).collect(Collectors.joining(", "));

        return statement.append(columnsStatement).append(')').toString();
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(this.columns);
    }

    public void addColumn(final Column column) {
        this.columns.add(column);
    }

    public boolean hasIdentifierKey() {
        return this.columns.stream()
                .anyMatch(Column::isIdentifierKey);
    }
}
