package persistence.entity.persistencecontext;

import persistence.sql.ddl.PrimaryKeyClause;

import java.util.HashMap;
import java.util.Map;

public class EntityCache {
    private final Map<EntityKey, Object> entityCache;

    public EntityCache() {
        this.entityCache = new HashMap<>();
    }

    public void put(Object entity) {
        var clazz = entity.getClass();
        entityCache.put(new EntityKey(clazz, PrimaryKeyClause.primaryKeyValue(entity)), entity);
    }

    public void put(Long key, Object entity) {
        var clazz = entity.getClass();
        entityCache.put(new EntityKey(clazz, key), entity);
    }

    public Object get(Class<?> clazz, Long key) {
        var entityKey = new EntityKey(clazz, key);
        var cachedEntity = entityCache.get(entityKey);
        if (cachedEntity == null) {
            return null;
        }
        return entityCache.get(entityKey);
    }

    public void remove(Object entity) {
        var key = new EntityKey(entity);
        entityCache.remove(key);
    }
}
