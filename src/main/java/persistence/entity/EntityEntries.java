package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityEntries {
    private final Map<String, EntityEntry> entityEntriesMap;

    public EntityEntries() {
        this.entityEntriesMap = new HashMap<>();
    }

    public Status getStatus(String cacheKey) {
        if (!entityEntriesMap.containsKey(cacheKey)) {
            return null;
        }
        return entityEntriesMap.get(cacheKey).getStatus();
    }

    public void setStatus(String cacheKey, Status status) {
        if (!entityEntriesMap.containsKey(cacheKey)) {
            entityEntriesMap.put(cacheKey, new EntityEntry(status));
        } else {
            entityEntriesMap.get(cacheKey).setStatus(status);
        }
    }

    public void removeStatus(String cacheKey) {
        entityEntriesMap.remove(cacheKey);
    }
}
