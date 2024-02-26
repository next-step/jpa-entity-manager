package persistence.sql.dml;

import persistence.sql.domain.Column;
import persistence.sql.domain.DataType;
import persistence.sql.domain.IdColumn;
import persistence.sql.domain.Table;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {
    private static final String UPDATE_QUERY_TEMPLATE = "UPDATE %s SET %s WHERE %s = %s";

    public String build(Object object) {
        Table table = Table.from(object.getClass());
        IdColumn idColumn = table.getIdColumn();
        String updateClause = getUpdateClause(object, table.getColumns());
        Object keyValue = getValue(object, idColumn);
        return String.format(UPDATE_QUERY_TEMPLATE, table.getName(), updateClause, idColumn.getName(), keyValue);
    }

    private String getUpdateClause(Object object, List<Column> columns) {
        Map<String, String> columnClause = getColumnClause(object, columns);
        return columnClause.entrySet().stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    private LinkedHashMap<String, String> getColumnClause(Object object, List<Column> columns) {
        return columns.stream()
                .filter(column -> !column.isId())
                .collect(Collectors.toMap(Column::getName, column -> getDmlName(object, column),
                        (existingValue, newValue) -> existingValue, LinkedHashMap::new));
    }

    private String getDmlName(Object object, Column column) {
        Object value = getValue(object, column);
        DataType columnType = column.getType();
        if (columnType.isVarchar()) {
            return String.format("'%s'", value);
        }
        return value.toString();
    }

    private Object getValue(Object object, Column column) {
        try {
            Field field = column.getField();
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field: " + column.getName(), e);
        }
    }
}
