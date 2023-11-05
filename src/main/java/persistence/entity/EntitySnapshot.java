package persistence.entity;

import domain.Snapshot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntitySnapshot {
    private final Map<Integer, Snapshot> snapshotMap;

    public EntitySnapshot() {
        this.snapshotMap = new ConcurrentHashMap<>();
    }

    public Snapshot getSnapshot(Integer hashCode) {
        return snapshotMap.get(hashCode);
    }

    public Object getEntity(Integer hashCode) {
        return getSnapshot(hashCode).getObject();
    }

    public <I> Snapshot save(Integer hashCode, I input, Object data) {
        snapshotMap.put(hashCode, new Snapshot(input, data));

        return getSnapshot(hashCode);
    }

    public boolean isEntityInSnapshot(Integer hashCode) {
        return snapshotMap.containsKey(hashCode);
    }

    public int size() {
        return snapshotMap.size();
    }

    public void clear() {
        snapshotMap.clear();
    }

    public Map<Integer, Snapshot> exploreInSnapshot(EntityPersistenceContext context) {
        Map<Integer, Snapshot> result = new ConcurrentHashMap<>();

        snapshotMap.forEach((id, snapshot) -> {
            if (context.isEntityInContext(id) && snapshot.getObject().equals(context.getEntity(id).getObject())) {
                return;
            }

            if (isEntityInSnapshot(id)) {
                result.put(id, snapshot);
            }

            if (context.isEntityInContext(id)) {
                result.put(id, context.getEntity(id));
            }
        });

        return result;
    }
}
