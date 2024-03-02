package persistence.entity;

import persistence.sql.model.Table;

import java.util.concurrent.ConcurrentHashMap;

public class EntityMetaCache {

    private final ConcurrentHashMap<Class<?>, Table> tables;

    public EntityMetaCache() {
        this.tables = new ConcurrentHashMap<>();
    }

    public Table getTable(Class<?> clazz) {
        Table findTable = tables.get(clazz);

        if (findTable != null) {
            return findTable;
        }

        Table table = new Table(clazz);
        tables.put(clazz, table);

        return table;
    }
}
