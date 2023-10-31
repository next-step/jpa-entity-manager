package persistence.entity;

import persistence.exception.PersistenceException;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public class EntityEntries {
    private final Map<Object, EntityEntry> entityEntries;

    public EntityEntries() {
        this.entityEntries = new IdentityHashMap<>();
    }

    public void addEntityEntry(final Object entity, final Status status) {
        entityEntries.put(entity, new EntityEntry(status));
    }

    public Optional<EntityEntry> getEntityEntry(final Object entity) {
        return Optional.ofNullable(entityEntries.get(entity));
    }

    public void updateEntityEntryStatus(final Object entity, final Status status) {
        final EntityEntry entityEntry = getEntityEntry(entity)
                .orElseThrow(() -> new PersistenceException("EntityEntry 로 관리되고 있지 않은 Entity 입니다."));
        entityEntry.updateStatus(status);
    }
}
