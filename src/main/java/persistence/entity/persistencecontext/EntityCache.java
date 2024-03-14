package persistence.entity.persistencecontext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static persistence.sql.dml.clause.PrimaryKeyValue.getPrimaryKeyValue;

public class EntityCache {
    private final Map<EntityKey, Object> entityCache;

    public EntityCache() {
        this.entityCache = new HashMap<>();
    }

    public void put(Object entity, EntityKey key) {
        entityCache.put(key, entity);
    }

    public Optional<Object> get(EntityKey key) {
        Object cachedEntity = entityCache.get(key);
        if (cachedEntity == null) {
            return Optional.empty();
        }
        return Optional.of(entityCache.get(key));
    }

    public void remove(Object entity) {
        Class<?> clazz = entity.getClass();
        Long id = getPrimaryKeyValue(entity);
        EntityKey key = new EntityKey(clazz, id);
        entityCache.remove(key);
    }
}
