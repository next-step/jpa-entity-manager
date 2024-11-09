package persistence.entity.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import persistence.entity.EntityKey;
import persistence.entity.EntitySnapshot;
import persistence.entity.PersistenceContext;

public class DefaultPersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> context = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> snapshots = new HashMap<>();

    @Override
    public <T, ID> Optional<T> getEntity(ID id, Class<T> entityType) {
        EntityKey key = new EntityKey(id, entityType);
        return Optional.ofNullable(entityType.cast(context.get(key)));
    }

    @Override
    public void addEntity(Object entity) {
        EntityKey key = new EntityKey(entity);
        if (context.containsKey(key)) {
            return;
        }
        context.put(key, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey key = new EntityKey(entity);
        context.remove(key);
    }

    @Override
    public <ID> void addDatabaseSnapshot(ID id, Object snapshot) {
        EntityKey key = new EntityKey(id, snapshot.getClass());
        snapshots.put(key, new EntitySnapshot(snapshot));
    }

    @Override
    public <T, ID> EntitySnapshot getDatabaseSnapshot(ID id, Class<T> entityType) {
        EntityKey key = new EntityKey(id, entityType);
        return snapshots.get(key);
    }

}
