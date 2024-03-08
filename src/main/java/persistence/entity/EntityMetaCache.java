package persistence.entity;

import persistence.sql.model.Table;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EntityMetaCache {

    INSTANCE(new ConcurrentHashMap<>());


    private final Map<Class<?>, Table> tables;

    EntityMetaCache(Map<Class<?>, Table> tables) {
        this.tables = tables;
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
