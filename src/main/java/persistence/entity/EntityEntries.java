package persistence.entity;

import persistence.exception.PersistenceException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityEntries {
    private final Map<EntityKey, EntityEntry> entityEntries;

    public EntityEntries() {
        this.entityEntries = new HashMap<>();
    }

    public void addEntityEntry(final EntityKey key, final Status status) {
        entityEntries.put(key, new EntityEntry(key, status));
    }

    public Optional<EntityEntry> getEntityEntry(final EntityKey key) {
        return Optional.ofNullable(entityEntries.get(key));
    }

    public void updateEntityEntryStatus(final EntityKey entityKey, final Status status) {
        final EntityEntry entityEntry = getEntityEntry(entityKey)
                .orElseThrow(() -> new PersistenceException("EntityEntry 로 관리되고 있지 않은 Entity 입니다."));
        entityEntry.updateStatus(status);
    }
}
