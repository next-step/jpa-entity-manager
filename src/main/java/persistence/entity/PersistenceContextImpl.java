package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<EntityKey, Object> managedEntities = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> entitySnapshots = new HashMap<>();

    @Override
    public Object getEntity(EntityKey entityKey) {
        return managedEntities.get(entityKey);
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(EntityKey entityKey, Object entity) {
        return entitySnapshots.get(entityKey);
    }

    @Override
    public void addEntity(EntityKey entityKey, Object entity) {
        if (managedEntities.containsKey(entityKey)) {
            return;
        }

        final EntitySnapshot entitySnapshot = new EntitySnapshot(entity);
        managedEntities.put(entityKey, entity);
        entitySnapshots.put(entityKey, entitySnapshot);
    }

    @Override
    public void removeEntity(EntityKey entityKey) {
        managedEntities.remove(entityKey);
        entitySnapshots.remove(entityKey);
    }
}
