package hibernate.dml;

import hibernate.entity.meta.column.ColumnType;
import hibernate.entity.meta.column.EntityColumn;

import java.util.Map;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    public static UpdateQueryBuilder INSTANCE = new UpdateQueryBuilder();

    private static final String UPDATE_QUERY = "update %s set %s where %s = %s;";

    private static final String UPDATE_SET_QUERY_FORMAT = "%s = %s";
    private static final String UPDATE_SET_QUERY_VALUE_FORMAT = "'%s'";
    private static final String UPDATE_SET_QUERY_DELIMITER = ", ";

    private UpdateQueryBuilder() {
    }

    public String generateQuery(
            final String tableName,
            final Map<EntityColumn, Object> fieldValues,
            final EntityColumn entityId,
            final Object id
    ) {
        return String.format(UPDATE_QUERY, tableName, parseSetQueries(fieldValues), entityId.getFieldName(), id);
    }

    private String parseSetQueries(final Map<EntityColumn, Object> fieldValues) {
        return fieldValues.entrySet()
                .stream()
                .map(entry -> parseSetQuery(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(UPDATE_SET_QUERY_DELIMITER));
    }

    private String parseSetQuery(final EntityColumn entityColumn, final Object entity) {
        return String.format(UPDATE_SET_QUERY_FORMAT, entityColumn.getFieldName(), parseFieldValue(entityColumn, entity));
    }

    private String parseFieldValue(final EntityColumn entityColumn, final Object entity) {
        if (entityColumn.getColumnType() == ColumnType.VAR_CHAR) {
            return String.format(UPDATE_SET_QUERY_VALUE_FORMAT, entity);
        }
        return entity.toString();
    }
}
