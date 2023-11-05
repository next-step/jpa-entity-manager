package persistence.entity;

import domain.Snapshot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceContextImpl implements PersistenceContext {
    private final EntityEntry entityEntry;
    private final EntityPersistenceContext entityContext;
    private final Map<Integer, Snapshot> snapshotMap;

    PersistenceContextImpl() {
        this.entityEntry = new EntityEntry();
        this.entityContext = new EntityPersistenceContext();
        this.snapshotMap = new ConcurrentHashMap<>();
    }

    @Override
    public <T, I> T getEntity(Integer key, EntityPersister<T> persister, I input) {
        if (entityEntry.isManaged(key)) {
            return (T) entityContext.getEntity(key).getObject();
        }

        entityEntry.loading(key);

        Snapshot snapshot = getDatabaseSnapshot(key, persister, input);

        if (snapshot.getObject() == null) {
            entityEntry.clear(key);
            return null;
        }

        addEntity(key, snapshot);

        entityEntry.managed(key);

        return (T) snapshot.getObject();
    }

    @Override
    public Object addEntity(Integer key, Object id, Object entity) {
        if (entity == null) {
            return null;
        }

        addEntity(key, new Snapshot(id, entity));

        return entityContext.getEntity(key).getObject();
    }

    @Override
    public Object addEntity(Integer key, Snapshot snapshot) {
        entityEntry.saving(key);

        return entityContext.save(key, snapshot);
    }

    @Override
    public void removeEntity(Integer key) {
        entityEntry.deleted(key);

        entityContext.delete(key);
    }

    @Override
    public <T, I> Snapshot getDatabaseSnapshot(Integer key, EntityPersister<T> persister, I input) {
        Object data = persister.findById(input);
        snapshotMap.put(key, new Snapshot(input, data));
        return snapshotMap.get(key);
    }

    @Override
    public Map<Integer, Snapshot> comparison() {
        if (snapshotMap.size() >= entityContext.size()) {
            return exploreInSnapshot();
        }

        return entityContext.exploreInContext(snapshotMap);
    }

    @Override
    public void flush(Map<String, EntityPersister<?>> persister) {
        Map<Integer, Snapshot> map = comparison();

        if (map.isEmpty()) {
            return;
        }

        entityEntry.getEntry().forEach((hashcode, status) -> {
            Object object = map.get(hashcode).getObject();
            EntityPersister<?> entityPersister = persister.get(object.getClass().getName());

            if (status.isSaving()) {
                entityPersister.insert(object);
                entityEntry.managed(hashcode);
                return;
            }

            if (status.isDeleted()) {
                entityPersister.delete(object);
                entityEntry.gone(hashcode);
                return;
            }

            entityPersister.update(object, map.get(entityContext.getEntity(hashcode)).getId());
            entityEntry.managed(hashcode);
        });

        clear();
    }

    private void clear() {
        entityContext.clear();
        snapshotMap.clear();
        entityEntry.clear();
    }

    private Map<Integer, Snapshot> exploreInSnapshot() {
        Map<Integer, Snapshot> result = new ConcurrentHashMap<>();

        snapshotMap.forEach((id, snapshot) -> {
            if (entityContext.isEntityInContext(id) && snapshot.getObject().equals(entityContext.getEntity(id).getObject())) {
                return;
            }

            if (snapshotMap.containsKey(id)) {
                result.put(id, snapshot);
            }

            if (entityContext.isEntityInContext(id)) {
                result.put(id, entityContext.getEntity(id));
            }
        });

        return result;
    }
}
