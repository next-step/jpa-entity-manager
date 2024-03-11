package persistence.entity.persistencecontext;

import persistence.sql.ddl.PrimaryKeyClause;

import java.util.HashMap;
import java.util.Map;

public class Snapshot {
    private final Map<EntityKey, Object> snapshot;

    public Snapshot() {
        this.snapshot = new HashMap<>();
    }

    public void put(Object entity) {
        var clazz = entity.getClass();
        snapshot.put(new EntityKey(clazz, PrimaryKeyClause.primaryKeyValue(entity)), entity);
    }

    public void put(Long key, Object entity) {
        var clazz = entity.getClass();
        snapshot.put(new EntityKey(clazz, key), entity);
    }

    public void remove(Object entity) {
        var key = new EntityKey(entity);
        snapshot.remove(key);
    }

    public Object get(Class<?> clazz, Long key) {
        var entityKey = new EntityKey(clazz, key);
        var cachedEntity = snapshot.get(entityKey);
        if (cachedEntity == null) {
            return null;
        }
        return snapshot.get(entityKey);
    }
}
