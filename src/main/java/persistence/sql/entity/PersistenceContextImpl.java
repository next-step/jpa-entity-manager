package persistence.sql.entity;

import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<Class<?>, Map<Long, Object>> managedEntities = new HashMap<>();

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
    }

    @Override
    public void removeEntity(Class<?> clazz, Long id) {
        if (containsEntity(clazz, id)) {
            managedEntities.get(clazz).remove(id);
        }
    }

    @Override
    public boolean containsEntity(Class<?> clazz, Long id) {
        return managedEntities.containsKey(clazz) && managedEntities.get(clazz).containsKey(id);
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
