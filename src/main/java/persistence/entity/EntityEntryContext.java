package persistence.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityEntryContext {

    private final Map<EntityCacheKey, EntityEntry> context = new ConcurrentHashMap<>();

    public EntityEntry get(Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity);
        return getEntry(entityCacheKey);
    }

    public EntityEntry get(Class<?> clazz, Object id) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(clazz, id);
        return getEntry(entityCacheKey);
    }

    private EntityEntry getEntry(EntityCacheKey entityCacheKey) {
        EntityEntry cachedEntry = context.get(entityCacheKey);
        if (cachedEntry == null){
            SimpleEntityEntry newEntry = new SimpleEntityEntry();
            context.put(entityCacheKey, newEntry);
            return newEntry;
        }
        return cachedEntry;
    }
}
