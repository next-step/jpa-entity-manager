package persistence.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<Long, Object> entitiesByKey = new HashMap<>();
    private final Map<Long, Object> entitySnapshotsByKey = new HashMap<>();

    @Override
    public Object getEntity(Long id) {
        return entitiesByKey.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        entitiesByKey.put(id, entity);
        entitySnapshotsByKey.put(id, createSnapshot(entity));
    }

    @Override
    public void removeEntity(Long id) {
        entitiesByKey.remove(id);
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        Object cached = entitySnapshotsByKey.get(id);
        if (cached != null) {
            return cached;
        }

        Object snapshot = createSnapshot(entity);
        entitySnapshotsByKey.put(id, snapshot);

        return snapshot;
    }

    private Object createSnapshot(Object entity) {
        try {
            Class<?> entityClass = entity.getClass();
            Object snapshot = entityClass.getDeclaredConstructor().newInstance();
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(snapshot, field.get(entity));
            }
            return snapshot;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getCachedDatabaseSnapshot(Long id) {
        return entitySnapshotsByKey.get(id);
    }
}
