package persistence.sql.dml;

import persistence.sql.domain.Column;
import persistence.sql.domain.Table;

import java.util.List;
import java.util.stream.Collectors;

public class SelectAllQueryBuilder {
    private static final String FIND_ALL_QUERY_TEMPLATE = "SELECT %s FROM %s";
    private static final String COLUMN_DELIMITER = ", ";

    public String build(Class<?> target) {
        Table table = Table.from(target);
        String columnNames = getColumnsNames(table.getColumns());
        return String.format(FIND_ALL_QUERY_TEMPLATE, columnNames, table.getName());
    }

    private String getColumnsNames(List<Column> columns) {
        return columns.stream()
                .map(Column::getName)
                .collect(Collectors.joining(COLUMN_DELIMITER));
    }
}
