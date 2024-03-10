package persistence.core;

import java.util.HashMap;
import java.util.Map;

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
    public void removeEntity(Object entity) {
        managedEntities.values().remove(entity);
    }

    @Override
    public void getDatabaseSnapshot(Long id, Object entity) {
        EntityKey entityKey = entityKeyManager.createKey(entity.getClass(), id);
        snapshotEntities.put(entityKey, new Snapshot(entity));
    }

    @Override
    public void persist(Long id, Object entity) {
        EntityKey entityKey = entityKeyManager.createKey(entity.getClass(), id);
        addEntity(id, entity);
        managedEntities.put(entityKey, entity);
        snapshotEntities.put(entityKey, new Snapshot(entity));

    }

}
