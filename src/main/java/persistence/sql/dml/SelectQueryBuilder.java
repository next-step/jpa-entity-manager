package persistence.sql.dml;

import persistence.sql.AbstractQueryBuilder;
import persistence.sql.EntityMetadata;

public class SelectQueryBuilder extends AbstractQueryBuilder {

    public String getSelectAllQuery(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        return String.format(
            "SELECT %s FROM %s",
            getColumnNamesQuery(entityMetadata),
            entityMetadata.getTableName()
        );
    }

    public String getSelectByIdQuery(Class<?> entityClass, Object id) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        return String.format(
            "SELECT %s FROM %s WHERE %s = %s",
            getColumnNamesQuery(entityMetadata),
            entityMetadata.getTableName(),
            getPrimaryKeyColumnNameQuery(entityMetadata),
            getIdQuery(entityMetadata, entityClass, id)
        );
    }

    public String getSelectCountQuery(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        return String.format(
            "SELECT COUNT(%s) FROM %s",
            getPrimaryKeyColumnNameQuery(entityMetadata),
            entityMetadata.getTableName()
        );
    }
}
