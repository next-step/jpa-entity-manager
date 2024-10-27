package persistence.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultPersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> context = new HashMap<>();

    @Override
    public <T, ID> Optional<T> getEntity(ID id, Class<T> entityType) {
        EntityKey key = new EntityKey(id, entityType);
        return Optional.ofNullable(entityType.cast(context.get(key)));
    }

    @Override
    public <T> void addEntity(T entity) {
        EntityKey key = new EntityKey(entity);
        if (context.containsKey(key)) {
            return;
        }
        context.put(key, entity);
    }

    @Override
    public <T> void removeEntity(T entity) {
        EntityKey key = new EntityKey(entity);
        context.remove(key);
    }
}
