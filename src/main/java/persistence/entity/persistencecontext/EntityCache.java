package persistence.entity.persistencecontext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static persistence.entity.generator.PrimaryKeyValueGenerator.primaryKeyValue;

public class EntityCache {
    private final Map<EntityKey, Object> entityCache;

    public EntityCache() {
        this.entityCache = new HashMap<>();
    }

    public void put(Object entity) {
        Class<?> clazz = entity.getClass();
        entityCache.put(new EntityKey(clazz, primaryKeyValue(entity)), entity);
    }

    public Optional<Object> get(Class<?> clazz, Long key) {
        EntityKey entityKey = new EntityKey(clazz, key);
        Object cachedEntity = entityCache.get(entityKey);
        if (cachedEntity == null) {
            return Optional.empty();
        }
        return Optional.of(entityCache.get(entityKey));
    }

    public void remove(Object entity) {
        EntityKey key = new EntityKey(entity);
        entityCache.remove(key);
    }
}
