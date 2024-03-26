package persistence;

import persistence.entity.Status;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class DefaultPersistenceContext<T> implements PersistenceContext<T> {

    private final HashMap<Long, T> entitiesByKey = new HashMap<>();
    private final HashMap<Long, T> entitySnapshotsByKey = new HashMap<>();
    private final HashMap<Object, Status> entityEntries = new HashMap<>();

    @Override
    public T getEntity(Long id) {
        return entitiesByKey.get(id);
    }

    @Override
    public void addEntityEntry(T entity, Status status) {
        entityEntries.put(entity, status);
    }

    @Override
    public void addEntityEntry(Long id, T entity) {
        entitiesByKey.put(id, entity);
        entityEntries.put(entity, Status.MANAGED);
    }

    @Override
    public T removeEntity(Long id) {
        return entitiesByKey.remove(id);
    }

    @Override
    public T getCachedDatabaseSnapshot(Long id, T entity) {
        T snapshot = entitySnapshotsByKey.get(id);

        if (snapshot != null) {
            return snapshot;
        }

        T snapshotEntity = getSnapshotEntity(entity);

        return entitySnapshotsByKey.putIfAbsent(id, snapshotEntity);
    }

    private static <T> T getSnapshotEntity(T entity) {
        Class<?> entityClass = entity.getClass();
        T snapshotEntity;
        try {
            snapshotEntity = (T) entityClass.getDeclaredConstructor().newInstance();
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(snapshotEntity, field.get(entity));
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException("not work getSnapshotEntity", e);
        }
        return snapshotEntity;
    }
}
