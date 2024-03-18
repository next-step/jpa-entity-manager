package persistence.entity.persistencecontext;

import persistence.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

public class Snapshot {
    private final Map<EntityKey, Object> snapshot;

    public Snapshot() {
        this.snapshot = new HashMap<>();
    }

    public void put(Object entity, EntityKey key) {
        snapshot.put(key, entity);
    }

    public void remove(EntityKey key) {
        snapshot.remove(key);
    }

    public <T> T get(EntityKey key) {
        return (T) snapshot.get(key);
    }

    public <T> boolean isDirty(T entity) {

        Class<?> clazz = entity.getClass();
        Long primaryKeyValue = new PrimaryKey(entity).value();
        EntityKey key = new EntityKey(clazz, primaryKeyValue);
        T searchedEntity = (T) snapshot.get(key);
        return !entity.equals(searchedEntity);
    }
}
