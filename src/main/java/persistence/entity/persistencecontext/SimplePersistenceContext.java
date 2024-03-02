package persistence.entity.persistencecontext;

import java.util.HashMap;
import java.util.Map;
import persistence.entity.EntityEntry;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entities;
    private final Map<EntityKey, EntitySnapshot> snapshot;
    private final Map<EntityKey, EntityEntry> entityEntryMap;

    public SimplePersistenceContext() {
        this.entities = new HashMap<>();
        this.snapshot = new HashMap<>();
        this.entityEntryMap = new HashMap<>();
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
        EntityKey key = EntityKey.from(entity);
        entities.put(key, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey key = EntityKey.from(entity);

        entities.remove(key);
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Object entity) {
        return snapshot.get(EntityKey.from(entity));
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(Object entity) {
        return snapshot.computeIfAbsent(EntityKey.from(entity), k -> EntitySnapshot.from(entity));
    }

    @Override
    public void setEntityEntry(Object entity, EntityEntry entityEntry) {
        entityEntryMap.put(EntityKey.from(entity), entityEntry);
    }

    @Override
    public EntityEntry getEntityEntry(Object entity) {
        return entityEntryMap.get(EntityKey.from(entity));
    }
}
