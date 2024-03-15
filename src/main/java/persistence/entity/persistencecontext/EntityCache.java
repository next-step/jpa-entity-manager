package persistence.entity.persistencecontext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public void remove(EntityKey key) {
        entityCache.remove(key);
    }
}
