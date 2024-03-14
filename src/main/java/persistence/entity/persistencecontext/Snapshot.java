package persistence.entity.persistencecontext;

import java.util.HashMap;
import java.util.Map;

import static persistence.entity.generator.PrimaryKeyValueGenerator.primaryKeyValue;

public class Snapshot {
    private final Map<EntityKey, Object> snapshot;

    public Snapshot() {
        this.snapshot = new HashMap<>();
    }

    public void put(Object entity) {
        Class<?> clazz = entity.getClass();
        snapshot.put(new EntityKey(clazz, primaryKeyValue(entity)), entity);
    }

    public void remove(Object entity) {
        EntityKey key = new EntityKey(entity);
        snapshot.remove(key);
    }

    public <T> T get(Class<?> clazz, Long key) {
        EntityKey entityKey = new EntityKey(clazz, key);
        Object cachedEntity = snapshot.get(entityKey);
        if (cachedEntity == null) {
            return null;
        }
        return (T) snapshot.get(entityKey);
    }
}
