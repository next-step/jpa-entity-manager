package persistence.entity;

import persistence.sql.domain.DatabaseTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityCacheKey, Object> cache = new ConcurrentHashMap<>();


    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(clazz, id);
        return (T) cache.get(entityCacheKey);
    }

    @Override
    public void addEntity(Object entity) {
        String id = new DatabaseTable(entity).getPrimaryColumn().getColumnValue();
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), id);
        cache.put(entityCacheKey, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        String id = new DatabaseTable(entity).getPrimaryColumn().getColumnValue();
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), id);
        cache.remove(entityCacheKey);
    }
}
