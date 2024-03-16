package persistence.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultPersistenceContext implements PersistenceContext {

    private EntityKeyManager entityKeyManager;
    private Map<EntityKey, Object> managedEntities;
    private Map<EntityKey, Snapshot> snapshotEntities;
    private Map<EntityKey, EntityEntry> entityEntries;

    public DefaultPersistenceContext() {
        this.entityKeyManager = new EntityKeyManager();
        this.managedEntities = new HashMap<>();
        this.snapshotEntities = new HashMap<>();
        this.entityEntries = new HashMap<>();
    }

    @Override
    public EntityKey getEntityKey(Class<?> clazz, Long id) {
        return entityKeyManager.from(clazz, id);
    }

    @Override
    public Object getEntity(EntityKey entityKey) {
        return managedEntities.get(entityKey);
    }

    @Override
    public void addEntity(EntityKey entityKey, Object entity) {
        managedEntities.put(entityKey, entity);
    }

    @Override
    public void removeEntity(EntityKey entityKey) {
        managedEntities.remove(entityKey);
    }

    @Override
    public void getDatabaseSnapshot(EntityKey entityKey) {
        snapshotEntities.put(entityKey, new Snapshot(managedEntities.get(entityKey)));
    }

    @Override
    public void addEntityEntry(EntityKey entityKey, EntityEntry entityEntry) {
        entityEntries.put(entityKey, entityEntry);
    }

    @Override
    public EntityEntry getEntityEntry(EntityKey entityKey) {
        return entityEntries.get(entityKey);
    }

    @Override
    public <T> List<T> dirtyCheck() {
        List<T> dirtyEntities = new ArrayList<>();
        managedEntities.forEach((key, entity) -> {
            if (isDirty(snapshotEntities.get(key), entity)) {
                dirtyEntities.add((T) entity);
            }
        });

        return dirtyEntities;
    }

    private boolean isDirty(Snapshot snapshot, Object entity) {
        Map<String, Object> snapshotEntity = snapshot.get();
        Snapshot currentEntity = new Snapshot(entity);
        Map<String, Object> stringObjectMap = currentEntity.get();

        return !snapshotEntity.equals(stringObjectMap);
    }

    public Snapshot getSnapshot(EntityKey entityKey) {
        return snapshotEntities.get(entityKey);
    }

    @Override
    public void clear() {
        managedEntities.clear();
        snapshotEntities.clear();
        entityEntries.clear();
    }

}
