package persistence.sql.dml;

import static persistence.sql.ddl.common.StringConstants.COLUMN_DEFINITION_DELIMITER;

import java.util.stream.Collectors;
import persistence.sql.AbstractQueryBuilder;
import persistence.sql.EntityColumn;
import persistence.sql.EntityColumnValue;
import persistence.sql.EntityMetadata;

public class InsertQueryBuilder extends AbstractQueryBuilder {

    public String getInsertQuery(Object entity) {
        EntityMetadata entityMetadata = new EntityMetadata(entity.getClass());

        return String.format(
            "INSERT INTO %s (%s) VALUES (%s)",
            entityMetadata.getTableName(),
            getColumnNamesQuery(entityMetadata, entity),
            getColumnValuesQuery(entityMetadata, entity)
        );
    }

    private String getColumnValuesQuery(EntityMetadata entityMetadata, Object entity) {
        if (entityMetadata.hasIdFrom(entity)) {
            return getColumnValuesQueryWithId(entityMetadata, entity);
        }

        return getColumnValuesQueryWithoutId(entityMetadata, entity);
    }

    private String getColumnNamesQuery(EntityMetadata entityMetadata, Object entity) {
        if (entityMetadata.hasIdFrom(entity)) {
            return getColumnNamesQueryWithId(entityMetadata);
        }

        return getColumnNamesQueryWithoutPrimaryKey(entityMetadata);
    }

    private String getColumnValuesQueryWithId(EntityMetadata entityMetadata, Object entity) {
        return entityMetadata.getEntityColumnValuesFrom(entity).stream()
            .map(EntityColumnValue::queryString)
            .collect(Collectors.joining(COLUMN_DEFINITION_DELIMITER));
    }

    private String getColumnValuesQueryWithoutId(EntityMetadata entityMetadata, Object entity) {
        return entityMetadata.getEntityColumns().stream()
            .filter(entityColumn -> !entityColumn.isPrimary())
            .map(entityColumn -> entityColumn.getEntityColumnValueFrom(entity))
            .map(EntityColumnValue::queryString)
            .collect(Collectors.joining(COLUMN_DEFINITION_DELIMITER));
    }

    private String getColumnNamesQueryWithId(EntityMetadata entityMetadata) {
        return entityMetadata.getEntityColumns().stream()
            .map(EntityColumn::getColumnName)
            .collect(Collectors.joining(COLUMN_DEFINITION_DELIMITER));
    }

    private String getColumnNamesQueryWithoutPrimaryKey(EntityMetadata entityMetadata) {
        return entityMetadata.getEntityColumns().stream()
            .filter(entityColumn -> !entityColumn.isPrimary())
            .map(EntityColumn::getColumnName)
            .collect(Collectors.joining(COLUMN_DEFINITION_DELIMITER));
    }
}
