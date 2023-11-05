package persistence.entity;

import domain.Snapshot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceContextImpl implements PersistenceContext {
    private final EntityEntry entityEntry;
    private final Map<Integer, Snapshot> contextMap;
    private final Map<Integer, Snapshot> snapshotMap;

    PersistenceContextImpl() {
        this.entityEntry = new EntityEntry();
        this.contextMap = new ConcurrentHashMap<>();
        this.snapshotMap = new ConcurrentHashMap<>();
    }

    @Override
    public <T, I> T getEntity(Integer key, EntityPersister<T> persister, I input) {
        if (entityEntry.isManaged(key)) {
            return (T) contextMap.get(key).getObject();
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

        return addEntity(key, new Snapshot(id, entity));
    }

    @Override
    public Object addEntity(Integer key, Snapshot snapshot) {
        entityEntry.saving(key);
        contextMap.put(key, snapshot);
        return contextMap.get(key).getObject();
    }

    @Override
    public void removeEntity(Integer key) {
        entityEntry.deleted(key);

        contextMap.remove(key);
    }

    @Override
    public boolean isEntityInSnapshot(Integer id) {
        return snapshotMap.containsKey(id);
    }

    public boolean isEntityInContext(Integer id) {
        return contextMap.containsKey(id);
    }

    @Override
    public <T, I> Snapshot getDatabaseSnapshot(Integer key, EntityPersister<T> persister, I input) {
        Object data = persister.findById(input);
        snapshotMap.put(key, new Snapshot(input, data));
        return snapshotMap.get(key);
    }

    @Override
    public Map<Integer, Snapshot> comparison() {
        if (snapshotMap.size() >= contextMap.size()) {
            return exploreInSnapshot();
        }

        return exploreInContext();
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

            entityPersister.update(object, map.get(contextMap.get(hashcode)).getId());
            entityEntry.managed(hashcode);
        });

        clear();
    }

    private void clear() {
        contextMap.clear();
        snapshotMap.clear();
        entityEntry.clear();
    }

    private Map<Integer, Snapshot> exploreInSnapshot() {
        Map<Integer, Snapshot> result = new ConcurrentHashMap<>();

        snapshotMap.forEach((id, snapshot) -> {
            if (isEntityInContext(id) && snapshot.getObject().equals(contextMap.get(id).getObject())) {
                return;
            }

            if (snapshotMap.containsKey(id)) {
                result.put(id, snapshot);
            }

            if (isEntityInContext(id)) {
                result.put(id, contextMap.get(id));
            }
        });

        return result;
    }

    private Map<Integer, Snapshot> exploreInContext() {
        Map<Integer, Snapshot> result = new ConcurrentHashMap<>();

        contextMap.forEach((id, snapshot) -> {
            if (isEntityInSnapshot(id) && snapshot.getObject().equals(snapshotMap.get(id).getObject())) {
                return;
            }

            if (contextMap.containsKey(id)) {
                result.put(id, snapshot);
            }

            if (isEntityInSnapshot(id)) {
                result.put(id, snapshotMap.get(id));
            }
        });

        return result;
    }
}
