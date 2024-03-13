package persistence.core;

import java.util.*;

public class DefaultPersistenceContext implements PersistenceContext {
    private EntityKeyManager entityKeyManager;
    private Map<EntityKey, Object> managedEntities;
    private Map<EntityKey, Snapshot> snapshotEntities;


    public DefaultPersistenceContext() {
        this.managedEntities = new HashMap<>();
        this.snapshotEntities = new HashMap<>();
        this.entityKeyManager = new EntityKeyManager();
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
    public void removeEntity(Long id, Object entity) {
        EntityKey entityKey = entityKeyManager.from(entity.getClass(), id);
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

    private boolean isDirty(Snapshot snapshot, Object entity) {
        Map<String, Object> snapshotEntity = snapshot.get();
        Snapshot currentEntity = new Snapshot(entity);
        Map<String, Object> stringObjectMap = currentEntity.get();

        return !snapshotEntity.equals(stringObjectMap);
    }

}
