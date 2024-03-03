package persistence.sql.entity;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<Long, Object> entitiesByKey = new HashMap<>();
    private final Map<Long, Object> entitySnapshotsByKey = new HashMap<>();

    @Override
    public Object getEntity(final Long id) {
        return entitiesByKey.get(id);
    }

    @Override
    public void addEntity(final Long id, final Object entity) {
        entitiesByKey.put(id, entity);
    }

    @Override
    public void removeEntity(final Object entity) {
        entitiesByKey.values().remove(entity);
    }
}
