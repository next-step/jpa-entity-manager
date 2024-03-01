package persistence.entity.context;

import java.util.HashMap;
import java.util.Map;

public class FirstLevelCache {
    private final Map<EntityKey, Object> map;

    public FirstLevelCache() {
        this.map = new HashMap<>();
    }

    public Object get(Class<?> entityClass, Long id) {
        EntityKey entityKey = getCacheKey(entityClass, id);
        return map.get(entityKey);
    }

    public void store(Class<?> entityClass, Long id, Object entity) {
        EntityKey entityKey = getCacheKey(entityClass, id);
        map.put(entityKey, entity);
    }

    public void delete(Class<?> entityClass, Long id) {
        EntityKey entityKey = getCacheKey(entityClass, id);
        map.remove(entityKey);
    }

    private static EntityKey getCacheKey(Class<?> entityClass, Long id) {
        if (id == null) {
            throw new RuntimeException("id is null");
        }
        return EntityKey.from(entityClass, id);
    }
}
