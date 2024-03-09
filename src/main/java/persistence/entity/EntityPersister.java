package persistence.entity;

import database.Database;
import persistence.sql.dml.DMLQueryBuilder;
import persistence.sql.model.PKColumn;
import persistence.sql.model.Table;

public class EntityPersister {

    private final Database database;

    public EntityPersister(Database database) {
        this.database = database;
    }

    public EntityId create(Object entity) {
        DMLQueryBuilder queryBuilder = createDMLQueryBuilder(entity);
        String insertQuery = queryBuilder.buildInsertQuery(entity);
        return database.executeQueryAndGetGeneratedKey(insertQuery);
    }

    public EntityId update(Object entity) {
        EntityBinder entityBinder = new EntityBinder(entity);
        EntityId id = entityBinder.getEntityId();

        DMLQueryBuilder queryBuilder = createDMLQueryBuilder(entity);
        String updateByIdQuery = queryBuilder.buildUpdateByIdQuery(entity, id);
        return database.executeQueryAndGetGeneratedKey(updateByIdQuery);
    }

    public void delete(Object entity) {
        EntityBinder entityBinder = new EntityBinder(entity);
        EntityId id = entityBinder.getEntityId();

        DMLQueryBuilder queryBuilder = createDMLQueryBuilder(entity);
        String deleteByIdQuery = queryBuilder.buildDeleteByIdQuery(id);

        database.execute(deleteByIdQuery);
    }

    private DMLQueryBuilder createDMLQueryBuilder(Object entity) {
        Table table = createTable(entity);
        return new DMLQueryBuilder(table);
    }

    private Table createTable(Object entity) {
        EntityMetaCache entityMetaCache = EntityMetaCache.INSTANCE;
        Class<?> clazz = entity.getClass();
        return entityMetaCache.getTable(clazz);
    }
}
