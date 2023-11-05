package persistence.entity;

import domain.Snapshot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityPersistenceContext {
    private final Map<Integer, Snapshot> context;

    public EntityPersistenceContext() {
        this.context = new ConcurrentHashMap<>();
    }

    public Snapshot getEntity(Integer hashCode) {
        return context.get(hashCode);
    }

    public Object save(Integer hashCode, Snapshot snapshot) {
        context.put(hashCode, snapshot);

        return context.get(hashCode);
    }

    public void delete(Integer hashCode) {
        context.remove(hashCode);
    }

    public void clear() {
        context.clear();
    }

    public boolean isEntityInContext(Integer hashCode) {
        return context.containsKey(hashCode);
    }

    public int size() {
        return context.size();
    }

    public Map<Integer, Snapshot> exploreInContext(EntitySnapshot snapshotMap) {
        Map<Integer, Snapshot> result = new ConcurrentHashMap<>();

        context.forEach((hashCode, data) -> {
            if (snapshotMap.isEntityInSnapshot(hashCode) && data.getObject().equals(snapshotMap.getEntity(hashCode))) {
                return;
            }

            if (context.containsKey(hashCode)) {
                result.put(hashCode, data);
            }

            if (snapshotMap.isEntityInSnapshot(hashCode)) {
                result.put(hashCode, snapshotMap.getSnapshot(hashCode));
            }
        });

        return result;
    }
}
