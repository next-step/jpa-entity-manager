package persistence.entity;

import domain.Snapshot;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import persistence.exception.InvalidContextException;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<Integer, Snapshot> contextMap;
    private final Map<Integer, Snapshot> snapshotMap;

    PersistenceContextImpl() {
        this.contextMap = new ConcurrentHashMap<>();
        this.snapshotMap = new ConcurrentHashMap<>();
    }

    @Override
    public Object getEntity(Integer id) {
        if (isEntityInContext(id)) {
            return contextMap.get(id).getObject();
        }
        return null;
    }

    @Override
    public void addEntity(Integer key, Object id, Object entity) {
        if (entity == null) {
            return;
        }

        addEntity(key, new Snapshot(id, entity));
    }

    @Override
    public void addEntity(Integer key, Snapshot snapshot) {
        contextMap.put(key, snapshot);
    }

    @Override
    public void removeEntity(Integer key) {
        if (!isEntityInContext(key)) {
            throw new InvalidContextException();
        }
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
        return snapshotMap.put(key, new Snapshot(input, data));
    }

    @Override
    public Map<Integer, Snapshot> comparison() {
        if(snapshotMap.size() >= contextMap.size()) {
            return exploreInSnapshot();
        }

        return exploreInContext();
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
