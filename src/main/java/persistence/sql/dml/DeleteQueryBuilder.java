package persistence.sql.dml;

import persistence.sql.AbstractQueryBuilder;
import persistence.sql.EntityMetadata;

public class DeleteQueryBuilder extends AbstractQueryBuilder {

    public String getDeleteAllQuery(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        return String.format(
            "DELETE FROM %s",
            entityMetadata.getTableName()
        );
    }

    public String getDeleteByIdQuery(Class<?> entityClass, Object id) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        return String.format(
            "DELETE FROM %s WHERE %s = %s",
            entityMetadata.getTableName(),
            getPrimaryKeyColumnNameQuery(entityMetadata),
            getIdQuery(entityMetadata, entityClass, id)
        );
    }

    public String getDeleteQueryFromEntity(Object entity) {
        Class<?> entityClass = entity.getClass();

        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        return String.format(
            "DELETE FROM %s WHERE %s = %s",
            entityMetadata.getTableName(),
            getPrimaryKeyColumnNameQuery(entityMetadata),
            getIdQuery(entityMetadata, entity)
        );
    }
}
