package persistence.entity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EntityEntryContext {
    private final Map<EntityKey, EntityEntry> context = new ConcurrentHashMap<>();

    public EntityEntry getEntityEntry(EntityKey entityKey) {
        return context.getOrDefault(entityKey, new EntityEntry(entityKey));
    }

    public List<EntityKey> getDeletedEntityKey() {
        return context.values().stream()
                .filter(EntityEntry::isDeleted)
                .map(EntityEntry::getEntityKey)
                .collect(Collectors.toList());
    }
    public void addEntityEntry(EntityKey entityKey, EntityEntry entityEntry) {
        context.put(entityKey, entityEntry);
    }

    public void clear() {
        context.clear();
    }
}
