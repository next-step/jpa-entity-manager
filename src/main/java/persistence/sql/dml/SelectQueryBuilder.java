package persistence.sql.dml;

import persistence.sql.domain.Column;
import persistence.sql.domain.DataType;
import persistence.sql.domain.IdColumn;
import persistence.sql.domain.Table;

import java.util.List;
import java.util.stream.Collectors;

public class SelectQueryBuilder {
    private static final String SELECT_QUERY_TEMPLATE = "SELECT %s FROM %s";
    private static final String WHERE_CLAUSE_TEMPLATE = " WHERE %s = %s";
    private static final String COLUMN_DELIMITER = ", ";

    private static class InstanceHolder {
        private static final SelectQueryBuilder INSTANCE = new SelectQueryBuilder();
    }

    public static SelectQueryBuilder getInstance() {
        return InstanceHolder.INSTANCE;
    }


    public String build(Class<?> target, Object id) {
        Table table = Table.from(target);
        String columnsNames = getColumnsNames(table.getColumns());

        String selectQuery = String.format(SELECT_QUERY_TEMPLATE, columnsNames, table.getName());
        return selectQuery + whereClause(table, id);
    }

    private String getColumnsNames(List<Column> columns) {
        return columns.stream()
                .map(Column::getName)
                .collect(Collectors.joining(COLUMN_DELIMITER));
    }

    private String whereClause(Table table, Object id) {
        IdColumn idColumn = table.getIdColumn();
        String value = getDmlValue(id, idColumn);
        return String.format(WHERE_CLAUSE_TEMPLATE, idColumn.getName(), value);
    }

    private String getDmlValue(Object id, Column column) {
        DataType columnType = column.getType();
        if (columnType.isVarchar()) {
            return String.format("'%s'", id);
        }
        return id.toString();
    }
}
