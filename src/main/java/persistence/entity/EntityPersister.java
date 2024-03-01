package persistence.entity;

import database.Database;
import persistence.sql.dml.DMLQueryBuilder;
import persistence.sql.model.PKColumn;
import persistence.sql.model.Table;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EntityPersister {

    private final Database database;
    private final ConcurrentHashMap<Class<?>, Table> tables;
    private final ConcurrentHashMap<Class<?>, DMLQueryBuilder> queryBuilders;

    public EntityPersister(Database database) {
        this.database = database;
        this.tables = new ConcurrentHashMap<>();
        this.queryBuilders = new ConcurrentHashMap<>();
    }

    public void create(Object entity) {
        Class<?> clazz = entity.getClass();
        DMLQueryBuilder dmlQueryBuilder = getQueryBuilder(clazz);
        String insertQuery = dmlQueryBuilder.buildInsertQuery(entity);
        database.execute(insertQuery);
    }

    public <T> T read(Class<T> clazz, Object id) {
        DMLQueryBuilder queryBuilder = getQueryBuilder(clazz);
        String findByIdQuery = queryBuilder.buildFindByIdQuery(id);
        return database.executeQueryForObject(clazz, findByIdQuery);
    }

    public boolean isExist(Object entity) {
        Class<?> clazz = entity.getClass();
        DMLQueryBuilder queryBuilder = getQueryBuilder(clazz);

        Object id = getEntityId(entity);
        String findByIdQuery = queryBuilder.buildFindByIdQuery(id);

        List<?> results = database.executeQuery(clazz, findByIdQuery);
        return !results.isEmpty();
    }

    public void update(Object entity) {
        Class<?> clazz = entity.getClass();
        DMLQueryBuilder queryBuilder = getQueryBuilder(clazz);

        Object id = getEntityId(entity);
        String updateByIdQuery = queryBuilder.buildUpdateByIdQuery(entity, id);

        database.execute(updateByIdQuery);
    }

    public void delete(Object entity) {
        Class<?> clazz = entity.getClass();
        DMLQueryBuilder queryBuilder = getQueryBuilder(clazz);

        Object id = getEntityId(entity);
        String deleteByIdQuery = queryBuilder.buildDeleteByIdQuery(id);

        database.execute(deleteByIdQuery);
    }

    private Object getEntityId(Object entity) {
        Class<?> clazz = entity.getClass();
        Table table = getTable(clazz);

        EntityBinder entityBinder = new EntityBinder(entity);

        PKColumn pkColumn = table.getPKColumn();
        return entityBinder.getValue(pkColumn);
    }

    private Table getTable(Class<?> clazz) {
        Table findTable = tables.get(clazz);

        if (findTable != null) {
            return findTable;
        }

        Table table = new Table(clazz);
        tables.put(clazz, table);

        return table;
    }

    private DMLQueryBuilder getQueryBuilder(Class<?> clazz) {
        DMLQueryBuilder findQueryBuilder = queryBuilders.get(clazz);

        if (findQueryBuilder != null) {
            return findQueryBuilder;
        }

        Table table = getTable(clazz);
        DMLQueryBuilder queryBuilder = new DMLQueryBuilder(table);
        queryBuilders.put(clazz, queryBuilder);

        return queryBuilder;
    }
}
