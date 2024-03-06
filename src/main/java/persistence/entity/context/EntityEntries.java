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

    public boolean isReadable(EntityKey entityKey) {
        return entityEntryOf(entityKey).isReadable();
    }

    public boolean isAssignable(EntityKey entityKey) {
        return entityEntryOf(entityKey).isAssignable();
    }

    public boolean isRemoved(EntityKey entityKey) {
        return entityEntryOf(entityKey).isAlreadyRemoved();
    }

    public boolean isRemovable(EntityKey entityKey) {
        return entityEntryOf(entityKey).isRemovable();
    }

    private EntityEntry entityEntryOf(EntityKey entityKey) {
        return entityEntriesMap.computeIfAbsent(entityKey, key -> EntityEntry.create());
    }
}
