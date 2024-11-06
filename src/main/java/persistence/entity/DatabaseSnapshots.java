package persistence.entity;

import static persistence.util.ReflectionCopy.copy;

import java.util.HashMap;
import java.util.Map;

public class DatabaseSnapshots {

    private final Map<EntityKey, Object> databaseSnapshots;

    public DatabaseSnapshots() {
        this.databaseSnapshots = new HashMap<>();
    }

    public void addDatabaseSnapshot(Object entity) {
        long id = new LongTypeId(entity).getId();
        EntityKey entityKey = new EntityKey(id, entity.getClass().getName());
        Object copyEntity = copy(entity);

        databaseSnapshots.put(entityKey, copyEntity);
    }

    public Object getDatabaseSnapshot(Object entity) {
        long id = new LongTypeId(entity).getId();
        EntityKey entityKey = new EntityKey(id, entity.getClass().getName());
        return databaseSnapshots.get(entityKey);
    }

    public void clear() {
        databaseSnapshots.clear();
    }

}
