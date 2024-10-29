package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class DatabaseSnapshots {

    private final Map<EntityKey, Integer> databaseSnapshots;

    public DatabaseSnapshots() {
        this.databaseSnapshots = new HashMap<>();
    }

    public void addDatabaseSnapshot(Object entity) {
        long id = new LongTypeId(entity).getId();
        EntityKey entityKey = new EntityKey(id, entity.getClass().getName());
        databaseSnapshots.put(entityKey, entity.hashCode());
    }

    public int getDatabaseSnapshot(Object entity) {
        long id = new LongTypeId(entity).getId();
        EntityKey entityKey = new EntityKey(id, entity.getClass().getName());
        return databaseSnapshots.get(entityKey);
    }

}
