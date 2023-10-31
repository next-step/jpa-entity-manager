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
        contextMap.put(key, new Snapshot(id, entity));
    }

    @Override
    public void removeEntity(Integer key) {
        if (!isEntityInContext(key)) {
            throw new InvalidContextException();
        }
        contextMap.remove(key);
    }

    public boolean isEntityInContext(Integer id) {
        return contextMap.containsKey(id);
    }

    @Override
    public <T, I> void getDatabaseSnapshot(Integer key, EntityPersister<T> persister, I input) {
        if (!snapshotMap.containsKey(key)) {
            snapshotMap.put(key, new Snapshot(input, persister.findById(input)));
        }
    }

    @Override
    public Map<Integer, Snapshot> comparison() {
        Map<Integer, Snapshot> result = new ConcurrentHashMap<>();

        snapshotMap.forEach((id, snapshot) -> {
            if (isEntityInContext(id) && snapshot.getValues().equals(contextMap.get(id).getValues())) {
                return;
            }

            if (isEntityInContext(id)) {
                result.put(id, contextMap.get(id));
            }

            if (snapshotMap.containsKey(id)) {
                result.put(id, snapshot);
            }
        });

        return result;
    }
}
