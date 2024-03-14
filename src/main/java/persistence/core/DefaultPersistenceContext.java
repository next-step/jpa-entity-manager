package persistence.core;

import java.util.*;

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
    public Object getEntity(Class<?> clazz, Long id) {
        EntityKey entityKey = entityKeyManager.from(clazz, id);

        return managedEntities.get(entityKey);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        EntityKey entityKey = entityKeyManager.createKey(entity.getClass(), id);
        managedEntities.put(entityKey, entity);
    }

    @Override
    public void removeEntity(Long id, Class<?> clazz) {
        EntityKey entityKey = entityKeyManager.from(clazz, id);
        managedEntities.remove(entityKey);
    }

    @Override
    public void getDatabaseSnapshot(Long id, Object entity) {
        EntityKey entityKey = entityKeyManager.from(entity.getClass(), id);
        snapshotEntities.put(entityKey, new Snapshot(managedEntities.get(entityKey)));
    }

    public Snapshot getSnapshot(Class<?> clazz, Long id) {
        EntityKey entityKey = entityKeyManager.from(clazz, id);
        return snapshotEntities.get(entityKey);
    }

    public <T> List<T> dirtyCheck() {
        List<T> dirtyEntities = new ArrayList<>();
        managedEntities.forEach((key, entity) -> {
            if (isDirty(snapshotEntities.get(key), entity)) {
                dirtyEntities.add((T) entity);
            }
        });

        return dirtyEntities;
    }

    @Override
    public void addEntityEntry(Class<?> clazz, Long id, EntityEntry entryEntry) {
        EntityKey entityKey = entityKeyManager.from(clazz, id);
        entityEntries.put(entityKey, entryEntry);
    }

    @Override
    public EntityEntry getEntityEntry(Class<?> clazz, Long id) {
        System.out.println("entityEntries >> " + entityEntries);
        EntityKey entityKey = entityKeyManager.from(clazz, id);
        System.out.println("find entityEntries key >> " + entityKey);
        return entityEntries.get(entityKey);
    }

    private boolean isDirty(Snapshot snapshot, Object entity) {
        Map<String, Object> snapshotEntity = snapshot.get();
        Snapshot currentEntity = new Snapshot(entity);
        Map<String, Object> stringObjectMap = currentEntity.get();

        return !snapshotEntity.equals(stringObjectMap);
    }

}
