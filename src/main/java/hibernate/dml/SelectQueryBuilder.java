package hibernate.dml;

import hibernate.entity.EntityClass;
import hibernate.entity.column.EntityColumn;

import java.util.List;
import java.util.stream.Collectors;

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

    public String generateQuery(final EntityClass<?> entityClass, final Object id) {
        return String.format(SELECT_QUERY, parseColumnQueries(entityClass), entityClass.tableName(), entityClass.getEntityId().getFieldName(), id);
    }

    private String parseColumnQueries(final EntityClass<?> entityClass) {
        return entityClass.getEntityColumns()
                .stream()
                .map(EntityColumn::getFieldName)
                .collect(Collectors.joining(SELECT_QUERY_COLUMN_DELIMITER));
    }
}
