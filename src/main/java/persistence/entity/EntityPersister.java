package persistence.entity;

import database.Database;
import persistence.sql.dml.DMLQueryBuilder;
import persistence.sql.model.PKColumn;
import persistence.sql.model.Table;

public class EntityPersister {

    private final Database database;
    private final EntityMetaCache entityMetaCache;

    public EntityPersister(Database database, EntityMetaCache entityMetaCache) {
        this.database = database;
        this.entityMetaCache = entityMetaCache;
    }

    public EntityId create(Object entity) {
        DMLQueryBuilder queryBuilder = createDMLQueryBuilder(entity);
        String insertQuery = queryBuilder.buildInsertQuery(entity);
        return database.executeQueryAndGetGeneratedKey(insertQuery);
    }

    public EntityId update(Object entity) {
        DMLQueryBuilder queryBuilder = createDMLQueryBuilder(entity);

        EntityId id = getEntityId(entity);
        String updateByIdQuery = queryBuilder.buildUpdateByIdQuery(entity, id);
        return database.executeQueryAndGetGeneratedKey(updateByIdQuery);
    }

    public void delete(Object entity) {
        DMLQueryBuilder queryBuilder = createDMLQueryBuilder(entity);

        EntityId id = getEntityId(entity);
        String deleteByIdQuery = queryBuilder.buildDeleteByIdQuery(id);

        database.execute(deleteByIdQuery);
    }

    private DMLQueryBuilder createDMLQueryBuilder(Object entity) {
        Table table = createTable(entity);
        return new DMLQueryBuilder(table);
    }

    private Table createTable(Object entity) {
        Class<?> clazz = entity.getClass();
        return entityMetaCache.getTable(clazz);
    }

    private EntityId getEntityId(Object entity) {
        Table table = createTable(entity);

        EntityBinder entityBinder = new EntityBinder(entity);

        PKColumn pkColumn = table.getPKColumn();
        Object idValue = entityBinder.getValue(pkColumn);
        return new EntityId(idValue);
    }
}
