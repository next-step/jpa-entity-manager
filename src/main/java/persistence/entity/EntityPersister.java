package persistence.entity;

import database.Database;
import persistence.sql.dml.DMLQueryBuilder;
import persistence.sql.model.PKColumn;
import persistence.sql.model.Table;

import java.util.List;

public class EntityPersister {

    private final Database database;
    private final EntityMetaCache entityMetaCache;

    public EntityPersister(Database database, EntityMetaCache entityMetaCache) {
        this.database = database;
        this.entityMetaCache = entityMetaCache;
    }

    public void create(Object entity) {
        Class<?> clazz = entity.getClass();
        Table table = entityMetaCache.getTable(clazz);
        DMLQueryBuilder queryBuilder = new DMLQueryBuilder(table);
        String insertQuery = queryBuilder.buildInsertQuery(entity);
        database.execute(insertQuery);
    }

    public void update(Object entity) {
        Class<?> clazz = entity.getClass();
        Table table = entityMetaCache.getTable(clazz);
        DMLQueryBuilder queryBuilder = new DMLQueryBuilder(table);

        Object id = getEntityId(entity);
        String updateByIdQuery = queryBuilder.buildUpdateByIdQuery(entity, id);

        database.execute(updateByIdQuery);
    }

    public void delete(Object entity) {
        Class<?> clazz = entity.getClass();
        Table table = entityMetaCache.getTable(clazz);
        DMLQueryBuilder queryBuilder = new DMLQueryBuilder(table);

        Object id = getEntityId(entity);
        String deleteByIdQuery = queryBuilder.buildDeleteByIdQuery(id);

        database.execute(deleteByIdQuery);
    }

    private Object getEntityId(Object entity) {
        Class<?> clazz = entity.getClass();
        Table table = entityMetaCache.getTable(clazz);

        EntityBinder entityBinder = new EntityBinder(entity);

        PKColumn pkColumn = table.getPKColumn();
        return entityBinder.getValue(pkColumn);
    }
}
