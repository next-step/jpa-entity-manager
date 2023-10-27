package hibernate.dml;

import hibernate.entity.meta.column.ColumnType;
import hibernate.entity.meta.column.EntityColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InsertQueryBuilder {

    public static final InsertQueryBuilder INSTANCE = new InsertQueryBuilder();

    private static final String INSERT_QUERY = "insert into %s (%s) values (%s);";

    private static final String INSERT_COLUMN_QUERY_DELIMITER = ", ";
    private static final String INSERT_COLUMN_STRING_VALUE_FORMAT = "'%s'";

    private InsertQueryBuilder() {
    }

    public String generateQuery(final String tableName, final Map<EntityColumn, Object> fieldValues) {
        List<EntityColumn> entityColumns = new ArrayList<>(fieldValues.keySet());
        return String.format(INSERT_QUERY, tableName, parseColumnQueries(entityColumns), parseColumnValueQueries(entityColumns, fieldValues));
    }

    private String parseColumnQueries(final List<EntityColumn> entityColumns) {
        return entityColumns.stream()
                .map(EntityColumn::getFieldName)
                .collect(Collectors.joining(INSERT_COLUMN_QUERY_DELIMITER));
    }

    private Object parseColumnValueQueries(final List<EntityColumn> entityColumns, final Map<EntityColumn, Object> fieldValues) {
        return entityColumns.stream()
                .map(column -> parseFieldValue(column, fieldValues.get(column)))
                .collect(Collectors.joining(INSERT_COLUMN_QUERY_DELIMITER));
    }

    private String parseFieldValue(final EntityColumn entityColumn, final Object entity) {
        if (entityColumn.getColumnType() == ColumnType.VAR_CHAR) {
            return String.format(INSERT_COLUMN_STRING_VALUE_FORMAT, entity);
        }
        return entity.toString();
    }
}
