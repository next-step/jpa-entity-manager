package persistence.entity;

import persistence.sql.Entity;
import persistence.sql.Id;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceContext {
    private final Map<Id, Entity> entities = new ConcurrentHashMap<>();
    public <T> Optional<T> get(Class<T> entityClass, Object id) {
        final Entity entity = entities.get(new Id(entityClass, id));
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of((T) entity.getEntity());
    }

    public <T> void put(Class<T> clazz, Object instance) {
        entities.put(new Id(clazz, getIdValue(clazz, instance)), new Entity(instance));
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

    public void remove(Object entity) {
        entities.remove(new Id(entity));
    }
}
