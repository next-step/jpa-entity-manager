package hibernate.entity;

import hibernate.EntityEntry;

import java.util.Map;

public class EntityEntryContext {

    private final Map<Object, EntityEntry> entityEntries;

    public EntityEntryContext(final Map<Object, EntityEntry> entityEntries) {
        this.entityEntries = entityEntries;
    }

    public EntityEntry addEntityEntry(final Object entity, final EntityEntry entityEntry) {
        return entityEntries.put(entity, entityEntry);
    }

    public EntityEntry getEntityEntry(final Object entity) {
        return entityEntries.get(entity);
    }
}
