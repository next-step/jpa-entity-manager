package persistence.entity.persistencecontext;

import java.util.HashMap;
import java.util.Map;

import static persistence.sql.dml.value.PrimaryKeyValue.getPrimaryKeyValue;

public class Snapshot {
    private final Map<EntityKey, Object> snapshot;

    public Snapshot() {
        this.snapshot = new HashMap<>();
    }

    public void put(Object entity, EntityKey key) {
        snapshot.put(key, entity);
    }

    public void remove(Object entity) {
        Class<?> clazz = entity.getClass();
        Long id = getPrimaryKeyValue(entity);
        EntityKey key = new EntityKey(clazz, id);
        snapshot.remove(key);
    }

    public <T> T get(EntityKey key) {
        Object cachedEntity = snapshot.get(key);
        if (cachedEntity == null) {
            return null;
        }
        return (T) snapshot.get(key);
    }
}
