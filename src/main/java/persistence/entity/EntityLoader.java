package persistence.entity;

import database.Database;
import persistence.sql.dml.DMLQueryBuilder;
import persistence.sql.model.Table;

public class EntityLoader {

    private final Database database;
    private final EntityMetaCache entityMetaCache;

    public EntityLoader(Database database, EntityMetaCache entityMetaCache) {
        this.database = database;
        this.entityMetaCache = entityMetaCache;
    }

    public <T> T read(Class<T> clazz, Object id) {
        Table table = entityMetaCache.getTable(clazz);
        DMLQueryBuilder queryBuilder = new DMLQueryBuilder(table);
        String findByIdQuery = queryBuilder.buildFindByIdQuery(id);
        return database.executeQueryForObject(clazz, findByIdQuery);
    }
}
