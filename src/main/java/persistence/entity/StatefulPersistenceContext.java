package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class StatefulPersistenceContext implements PersistenceContext {
    private final Map<EntityKey, Object> context = new HashMap<>();

    @Override
    public boolean hasEntity(EntityKey key) {
        return context.get(key) != null;
    }

    @Override
    public <T> T findEntity(EntityKey<T> key) {
        return (T) context.get(key);
    }

    @Override
    public void persistEntity(Object entity) {
        context.put(new EntityKey(entity), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        context.remove(new EntityKey(entity));
    }
}
