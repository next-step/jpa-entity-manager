package hibernate.dml;

import hibernate.entity.EntityClass;
import hibernate.entity.column.ColumnType;
import hibernate.entity.column.EntityColumn;

import java.util.Map;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    private static final String UPDATE_QUERY = "update %s set %s where %s = %s;";

    private static final String UPDATE_SET_QUERY_FORMAT = "%s = %s";
    private static final String UPDATE_SET_QUERY_VALUE_FORMAT = "'%s'";
    private static final String UPDATE_SET_QUERY_DELIMITER = ", ";

    public UpdateQueryBuilder() {
    }

    public String generateQuery(final EntityClass<?> entityClass, Object entity) {
        Map<EntityColumn, Object> fieldValues = entityClass.getFieldValues(entity);
        return String.format(UPDATE_QUERY,
                entityClass.tableName(), parseSetQueries(fieldValues), entityClass.getEntityId().getFieldName(), entityClass.extractEntityId(entity));
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
