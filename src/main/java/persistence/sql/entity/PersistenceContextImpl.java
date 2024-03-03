package persistence.sql.entity;

import persistence.sql.meta.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<PrimaryKey, Object> entitiesByKey = new HashMap<>();
    private final Map<PrimaryKey, Object> entitySnapshotsByKey = new HashMap<>();

    @Override
    public Object getEntity(final PrimaryKey id) {
        return entitiesByKey.get(id);
    }

    @Override
    public void addEntity(final PrimaryKey id, final Object entity) {
        entitiesByKey.put(id, entity);
        entitySnapshotsByKey.put(id, entity);
    }

    @Override
    public void removeEntity(final PrimaryKey id) {
        entitiesByKey.remove(id);
        entitySnapshotsByKey.remove(id);
    }

    @Override
    public boolean isDirty(final PrimaryKey id, final Object entity) {
        if (entitySnapshotsByKey.containsKey(id)) {
            return !entitySnapshotsByKey.get(id).equals(entity);
        }

        return false;
    }
}
