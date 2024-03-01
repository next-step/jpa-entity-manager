package persistence.entity.persistencecontext;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entities;
    private final Map<EntityKey, EntitySnapshot> snapshot;

    public SimplePersistenceContext() {
        this.entities = new HashMap<>();
        this.snapshot = new HashMap<>();
    }

    @Override
    public Object getEntity(Class<?> clazz, Object id) {
        EntityKey key = EntityKey.of(clazz, id);
        if (entities.containsKey(key)) {
            return entities.get(key);
        }
        return null;
    }

    @Override
    public void addEntity(Object entity) {
        entities.put(EntityKey.from(entity), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        entities.remove(EntityKey.from(entity));
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Object entity) {
        return snapshot.get(EntityKey.from(entity));
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(Object entity) {
        return snapshot.computeIfAbsent(EntityKey.from(entity), k -> EntitySnapshot.from(entity));
    }
}
