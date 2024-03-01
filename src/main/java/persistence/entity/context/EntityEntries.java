package persistence.entity.context;

import java.util.HashMap;
import java.util.Map;

public class EntityEntries {
    private final Map<EntityKey, EntityEntry> entityEntriesMap;

    public EntityEntries() {
        this.entityEntriesMap = new HashMap<>();
    }

    public Status getStatus(Class<?> entityClass, Long id) {
        EntityKey cacheKey = getCacheKey(entityClass, id);

        if (!entityEntriesMap.containsKey(cacheKey)) {
            return null;
        }
        return entityEntriesMap.get(cacheKey).getStatus();
    }

    public void setStatus(Class<?> entityClass, Long id, Status status) {
        EntityKey cacheKey = getCacheKey(entityClass, id);

        if (!entityEntriesMap.containsKey(cacheKey)) {
            entityEntriesMap.put(cacheKey, new EntityEntry(status));
        } else {
            entityEntriesMap.get(cacheKey).setStatus(status);
        }
    }

    public void removeStatus(Class<?> entityClass, Long id) {
        EntityKey cacheKey = getCacheKey(entityClass, id);

        entityEntriesMap.remove(cacheKey);
    }

    private static EntityKey getCacheKey(Class<?> entityClass, Long id) {
        if (id == null) {
            throw new RuntimeException("id is null");
        }
        return EntityKey.from(entityClass, id);
    }
}
