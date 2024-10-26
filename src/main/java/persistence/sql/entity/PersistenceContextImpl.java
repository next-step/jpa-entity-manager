package persistence.sql.entity;

import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<Class<?>, Map<Long, Object>> managedEntities = new HashMap<>();
    private final Map<Class<?>, Map<Long, Object>> entitySnapshots = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> clazz, Long id) {
        if (!containsEntity(clazz, id)) {
            return null;
        }
        return clazz.cast(managedEntities.get(clazz).get(id));
    }

    @Override
    public void addEntity(Object entity, Long id) {
        Class<?> clazz = entity.getClass();
        Long idValue = getIdValue(entity);

        Map<Long, Object> longObjectMap = managedEntities.computeIfAbsent(clazz, aClass -> new HashMap<>());
        longObjectMap.put(idValue, entity);

        addSnapshot(id, entity);
    }

    @Override
    public void removeEntity(Class<?> clazz, Long id) {
        if (containsEntity(clazz, id)) {
            managedEntities.get(clazz).remove(id);
        }

        Map<Long, Object> entitySnapshot = entitySnapshots.get(clazz);
        if (entitySnapshot != null) {
            entitySnapshot.remove(id);
        }
    }

    @Override
    public boolean containsEntity(Class<?> clazz, Long id) {
        return managedEntities.containsKey(clazz) && managedEntities.get(clazz).containsKey(id);
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        Map<Long, Object> longObjectMap = entitySnapshots.get(entity.getClass());
        if (longObjectMap != null) {
            return longObjectMap.get(id);
        }
        return null;
    }

    @Override
    public void addSnapshot(Long id, Object entity) {
        Object snapshot = copySnapshot(entity, id);
        Map<Long, Object> longObjectMap = entitySnapshots.computeIfAbsent(entity.getClass(), aClass -> new HashMap<>());
        longObjectMap.put(id, snapshot);
    }

    @Override
    public boolean isDirty(Long id, Object currentEntity) {
        Class<?> clazz = currentEntity.getClass();
        Object entitySnapshot = entitySnapshots.getOrDefault(clazz, new HashMap<>()).get(id);
        return !currentEntity.equals(entitySnapshot);
    }

    private Object copySnapshot(Object entity, Long id) {
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
            throw new RuntimeException("스냅샷 생성 실패");
        }

        return snapshot;
    }

    private Long getIdValue(Object entity) {
        Class<?> clazz = entity.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    return (Long) field.get(entity);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("id값이 없음");
                }
            }
        }

        return null;
    }
}
