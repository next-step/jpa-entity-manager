package persistence.entity;

import domain.Snapshot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityPersistenceContext {
    private final Map<Integer, Snapshot> contextMap;

    public EntityPersistenceContext() {
        this.contextMap = new ConcurrentHashMap<>();
    }

    public Snapshot getEntity(Integer hashCode) {
        return contextMap.get(hashCode);
    }

    public Object save(Integer hashCode, Snapshot snapshot) {
        contextMap.put(hashCode, snapshot);

        return contextMap.get(hashCode);
    }

    public void delete(Integer hashCode) {
        contextMap.remove(hashCode);
    }

    public void clear() {
        contextMap.clear();
    }

    public boolean isEntityInContext(Integer hashCode) {
        return contextMap.containsKey(hashCode);
    }

    public int size() {
        return contextMap.size();
    }

    public Map<Integer, Snapshot> exploreInContext(EntitySnapshot snapshotMap) {
        Map<Integer, Snapshot> result = new ConcurrentHashMap<>();

        contextMap.forEach((id, snapshot) -> {
            if (snapshotMap.isEntityInSnapshot(id) && snapshot.getObject().equals(snapshotMap.getEntity(id))) {
                return;
            }

            if (contextMap.containsKey(id)) {
                result.put(id, snapshot);
            }

            if (snapshotMap.isEntityInSnapshot(id)) {
                result.put(id, snapshotMap.getSnapshot(id));
            }
        });

        return result;
    }
}
