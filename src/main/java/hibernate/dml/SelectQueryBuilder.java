package hibernate.dml;

import hibernate.entity.meta.column.EntityColumn;

import java.util.List;

public class SelectQueryBuilder {

    public static final SelectQueryBuilder INSTANCE = new SelectQueryBuilder();

    private static final String SELECT_QUERY = "select %s from %s where %s = %s;";

    private static final String SELECT_QUERY_COLUMN_DELIMITER = ", ";

    private SelectQueryBuilder() {
    }

    public String generateQuery(
            final String tableName,
            final List<String> fieldNames,
            final EntityColumn entityId,
            final Object id
    ) {
        return String.format(SELECT_QUERY, parseColumnQueries(fieldNames), tableName, entityId.getFieldName(), id);
    }

    private String parseColumnQueries(final List<String> fieldNames) {
        return String.join(SELECT_QUERY_COLUMN_DELIMITER, fieldNames);
    }
}
