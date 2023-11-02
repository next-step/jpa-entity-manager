package persistence.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityEntryContext {
    private final Map<EntityKey, EntityEntry> context = new ConcurrentHashMap<>();

    public EntityEntry getEntityEntry(EntityKey entityKey) {
        return context.getOrDefault(entityKey, new EntityEntry(entityKey));
    }
    public void addEntityEntry(EntityKey entityKey, EntityEntry entityEntry) {
        context.put(entityKey, entityEntry);
    }
}
