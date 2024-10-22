package persistence.sql.context.impl;

import persistence.sql.EntityLoaderFactory;
import persistence.sql.QueryBuilderFactory;
import persistence.sql.clause.DeleteQueryClauses;
import persistence.sql.clause.InsertColumnValueClause;
import persistence.sql.clause.UpdateQueryClauses;
import persistence.sql.common.util.NameConverter;
import persistence.sql.context.EntityPersister;
import persistence.sql.data.QueryType;
import persistence.sql.dml.Database;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.loader.EntityLoader;

import java.lang.reflect.Field;

public class DefaultEntityPersister implements EntityPersister {
    private final Database database;
    private final NameConverter nameConverter;

    public DefaultEntityPersister(Database database, NameConverter nameConverter) {
        this.database = database;
        this.nameConverter = nameConverter;
    }

    private static <T> MetadataLoader<?> getMetadataLoader(T entity) {
        EntityLoader<?> entityLoader = EntityLoaderFactory.getInstance().getLoader(entity.getClass());
        return entityLoader.getMetadataLoader();
    }

    @Override
    public <T> Object insert(T entity) {
        MetadataLoader<?> loader = getMetadataLoader(entity);

        InsertColumnValueClause clause = InsertColumnValueClause.newInstance(entity, nameConverter);

        String insertQuery = QueryBuilderFactory.getInstance().buildQuery(QueryType.INSERT, loader, clause);
        Object id = database.executeUpdate(insertQuery);
        updatePrimaryKeyValue(entity, id, loader);

        return entity;
    }

    @Override
    public <T> void update(T entity) {
        EntityLoader<?> entityLoader = EntityLoaderFactory.getInstance().getLoader(entity.getClass());
        MetadataLoader<?> loader = entityLoader.getMetadataLoader();

        UpdateQueryClauses updateQueryClauses = UpdateQueryClauses.builder(nameConverter)
                .where(entity, loader)
                .setColumnValues(entity, loader)
                .build();

        String mergeQuery = QueryBuilderFactory.getInstance()
                .buildQuery(QueryType.UPDATE, loader, updateQueryClauses.clauseArrays());
        database.executeUpdate(mergeQuery);
    }

    @Override
    public <T> void delete(T entity) {
        EntityLoader<?> entityLoader = EntityLoaderFactory.getInstance().getLoader(entity.getClass());
        MetadataLoader<?> loader = entityLoader.getMetadataLoader();

        DeleteQueryClauses deleteQueryClauses = DeleteQueryClauses.builder(nameConverter)
                .where(entity, loader)
                .build();

        String removeQuery = QueryBuilderFactory.getInstance().buildQuery(QueryType.DELETE, loader,
                deleteQueryClauses.clauseArrays());

        database.executeUpdate(removeQuery);
    }

    private void updatePrimaryKeyValue(Object entity, Object id, MetadataLoader<?> loader) {
        Field primaryKeyField = loader.getPrimaryKeyField();
        primaryKeyField.setAccessible(true);

        try {
            primaryKeyField.set(entity, id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
