package persistence.entity;

import persistence.model.EntityPrimaryKey;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<EntityKey, Object> entityCache = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> entityClass, Object id) {
        EntityKey cacheKey = createEntityKey(entityClass, id);
        return entityClass.cast(entityCache.get(cacheKey));
    }

    @Override
    public void addEntity(Object entityObject) {
        EntityKey cacheKey = createEntityKey(entityObject);
        System.out.println("after add : " + cacheKey);
        entityCache.put(cacheKey, entityObject);
    }

    @Override
    public void removeEntity(Object entityObject) {
        EntityKey cacheKey = createEntityKey(entityObject);
        entityCache.remove(cacheKey);
    }

    private EntityKey createEntityKey(Class<?> entityClass, Object id) {
        EntityPrimaryKey primaryKey = EntityPrimaryKey.build(entityClass, id);
        return new EntityKey(entityClass, primaryKey);
    }

    private EntityKey createEntityKey(Object entityObject) {
        EntityPrimaryKey primaryKey = EntityPrimaryKey.build(entityObject);
        return new EntityKey(entityObject.getClass(), primaryKey);
    }
}
