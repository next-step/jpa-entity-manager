package persistence.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PersistContextImpl implements PersistContext{
    private final Map<EntityCache, Object> context = new HashMap<>();

    @Override
    public <T, ID> Optional<T> getEntity(ID id, Class<T> entityClass) {
        EntityCache entityCache = new EntityCache(id, entityClass);
        return Optional.ofNullable(entityClass.cast(context.get(entityCache)));
    }

    @Override
    public void addEntity(Object entity) {
        EntityCache entityCache = new EntityCache(entity);
        context.put(entityCache, entity);
    }
}
