package persistence.entity;

import persistence.sql.Entity;
import persistence.sql.Id;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceContext {
    private final Map<Id, Entity> entities = new ConcurrentHashMap<>();

    public Object getEntity(Class<?> entityClass, Object id) {
        final Entity entity = entities.get(new Id(entityClass, id));
        if (entity == null) {
            return null;
        }
        return entity.getEntity();
    }

    public void addEntity(Object entity) {
        final Class<?> clazz = entity.getClass();
        entities.put(new Id(clazz, getIdValue(clazz, entity)), new Entity(entity));
    }

    public void removeEntity(Object entity) {
        entities.remove(new Id(entity));
    }

    public boolean contains(Object entity) {
        return entities.containsKey(new Id(entity));
    }

    private <T> Object getIdValue(Class<T> clazz, Object instance) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                field.setAccessible(true);
                try {
                    return field.get(instance);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new IllegalArgumentException("No id column found");
    }
}
