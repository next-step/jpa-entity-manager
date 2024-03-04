package persistence.entity.context;

import java.util.HashMap;
import java.util.Map;

public class EntityEntries {
    private final Map<EntityKey, EntityEntry> entityEntriesMap;

    public EntityEntries() {
        this.entityEntriesMap = new HashMap<>();
    }

    public void managed(EntityKey entityKey) {
        entityEntryOf(entityKey).managed();
    }

    public void deleted(EntityKey entityKey) {
        entityEntryOf(entityKey).deleted();
    }

    public void gone(EntityKey entityKey) {
        entityEntryOf(entityKey).gone();
    }

    public boolean canGet(EntityKey entityKey) {
        return statusOf(entityKey).canGet();
    }

    public boolean canAdd(EntityKey entityKey) {
        return statusOf(entityKey).canAdd();
    }

    public boolean isRemoved(EntityKey entityKey) {
        return statusOf(entityKey).isAlreadyRemoved();
    }

    public boolean canRemove(EntityKey entityKey) {
        return statusOf(entityKey).canDelete();
    }

    private Status statusOf(EntityKey entityKey) {
        EntityEntry entityEntry = entityEntriesMap.getOrDefault(entityKey, EntityEntry.NONE);
        return entityEntry.getStatus();
    }

    private EntityEntry entityEntryOf(EntityKey entityKey) {
        return entityEntriesMap.computeIfAbsent(entityKey, key -> EntityEntry.create());
    }
}
