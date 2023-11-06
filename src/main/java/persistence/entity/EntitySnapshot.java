package persistence.entity;

import domain.Snapshot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntitySnapshot {
    private final Map<Integer, Snapshot> snapshot;

    public EntitySnapshot() {
        this.snapshot = new ConcurrentHashMap<>();
    }

    public Snapshot getSnapshot(Integer hashCode) {
        return snapshot.get(hashCode);
    }

    public Object getEntity(Integer hashCode) {
        return getSnapshot(hashCode).getObject();
    }

    public <I> Snapshot save(Integer hashCode, I input, Object data) {
        snapshot.put(hashCode, new Snapshot(input, data));

        return getSnapshot(hashCode);
    }

    public boolean isEntityInSnapshot(Integer hashCode) {
        return snapshot.containsKey(hashCode);
    }

    public int size() {
        return snapshot.size();
    }

    public void clear() {
        snapshot.clear();
    }

    public Map<Integer, Snapshot> exploreInSnapshot(EntityPersistenceContext context) {
        Map<Integer, Snapshot> result = new ConcurrentHashMap<>();

        snapshot.forEach((hashCode, data) -> {
            if (context.isEntityInContext(hashCode) && data.getObject().equals(context.getEntity(hashCode).getObject())) {
                return;
            }

            if (isEntityInSnapshot(hashCode)) {
                result.put(hashCode, data);
            }

            if (context.isEntityInContext(hashCode)) {
                result.put(hashCode, context.getEntity(hashCode));
            }
        });

        return result;
    }
}
