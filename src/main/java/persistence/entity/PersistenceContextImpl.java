package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey, Object> managedEntities = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> entitySnapshots = new HashMap<>();
    private final Map<EntityKey, EntityEntry> entityEntries = new HashMap<>();

    @Override
    public void put(EntityKey entityKey, Object entity) {
        managedEntities.put(entityKey, entity);
        entitySnapshots.put(entityKey, EntitySnapshot.from(entity));
        entityEntries.put(entityKey, EntityEntry.createManaged());
    }

    @Override
    public boolean contains(EntityKey entityKey) {
        return managedEntities.containsKey(entityKey);
    }

    @Override
    public Object get(EntityKey entityKey) {
        return managedEntities.get(entityKey);
    }

    @Override
    public void remove(EntityKey entityKey) {
        managedEntities.remove(entityKey);
        entitySnapshots.remove(entityKey);
        entityEntries.get(entityKey).setDeleted();
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(EntityKey entityKey) {
        return entitySnapshots.get(entityKey);
    }
}
