package persistence.sql.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<EntityKey, Object> managedEntities = new HashMap<>();
    private final Map<EntityKey, Object> entitySnapshots = new HashMap<>();
    private final Map<Object, EntityEntry> managedEntries = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> clazz, Long id) {
        EntityKey entityKey = new EntityKey(id, clazz);

        if (!containsEntity(entityKey)) {
            return null;
        }
        Object entity = managedEntities.get(entityKey);
        return clazz.cast(entity);
    }

    @Override
    public void addEntity(Object entity, Long id) {
        EntityKey entityKey = new EntityKey(id, entity.getClass());
        managedEntities.put(entityKey, entity);

        addSnapshot(id, entity);
    }

    @Override
    public void removeEntity(Class<?> clazz, Long id) {
        EntityKey entityKey = new EntityKey(id, clazz);
        managedEntities.remove(entityKey);
        entitySnapshots.remove(entityKey);
    }

    @Override
    public boolean containsEntity(EntityKey entityKey) {
        return managedEntities.containsKey(entityKey);
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        EntityKey entityKey = new EntityKey(id, entity.getClass());
        return entitySnapshots.get(entityKey);
    }

    @Override
    public void addSnapshot(Long id, Object entity) {
        EntityKey entityKey = new EntityKey(id, entity.getClass());

        Object snapshot = copySnapshot(entity);
        entitySnapshots.put(entityKey, snapshot);
    }

    @Override
    public boolean isDirty(Long id, Object currentEntity) {
        EntityKey entityKey = new EntityKey(id, currentEntity.getClass());
        Object entitySnapshot = entitySnapshots.get(entityKey);

        if (entitySnapshot == null) {
            return true;
        }

        for (Field field : currentEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object currentValue;
            Object snapshotValue;
            try {
                currentValue = field.get(currentEntity);
                snapshotValue = field.get(entitySnapshot);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (!Objects.equals(currentValue, snapshotValue)) {
                return true;
            }
        }
        return false;

    }

    @Override
    public void addEntry(Object entityKey, EntityEntry entityEntry) {
        EntityEntry entry = managedEntries.get(entityKey);
        if (entry != null) {
            entry.updateStatus(entityEntry.getEntityStatus());
        }

        if (entry == null) {
            managedEntries.put(entityKey, entityEntry);
        }
    }

    @Override
    public void removePersistenceContext(EntityKey entityKey, Object entity) {
        managedEntities.remove(entityKey);
        entitySnapshots.remove(entityKey);
        managedEntries.remove(entity);
    }


    private Object copySnapshot(Object entity) {
        Class<?> clazz = entity.getClass();
        Object snapshot;
        try {
            snapshot = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(entity);
                field.set(snapshot, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("스냅샷 생성 실패", e);
        }

        return snapshot;
    }

}
