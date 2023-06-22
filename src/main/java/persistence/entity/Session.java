package persistence.entity;

import persistence.sql.Entity;
import persistence.sql.Id;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    private final Map<Id, Entity> entities = new ConcurrentHashMap<>();
    public <T> T get(Class<T> entityClass, Object id) {
        final Entity entity = entities.get(new Id(entityClass, id));
        if (entity == null) {
            return null;
        }
        return (T) entity;
    }

    public <T> void put(Class<T> clazz, Long id, Object instance) {
        entities.put(new Id(clazz, id), new Entity(instance));
    }
}
