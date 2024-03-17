package persistence.sql.entity.impl;

import persistence.sql.entity.PersistenceContext;
import persistence.sql.meta.Columns;

import java.util.HashMap;
import java.util.Map;

import static persistence.sql.meta.simple.SimpleEntityMetaCreator.createColumnValues;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<EntityKey, Object> entitiesByKey = new HashMap<>();
    private final Map<EntityKey, Columns> entitySnapshotsByKey = new HashMap<>();
    private final Map<EntityKey, EntityEntry> entityEntryByKey = new HashMap<>();

    @Override
    public Object getEntity(final EntityKey key) {
        return entitiesByKey.get(key);
    }

    @Override
    public void addEntity(final EntityKey key, final Object entity, final EntityEntry entityEntry) {
        addEntity(key, entity);
        addEntityEntry(key, entityEntry);
    }

    private void addEntity(final EntityKey key, final Object entity) {
        entitiesByKey.put(key, entity);

        final Columns columnValues = createColumnValues(entity);
        entitySnapshotsByKey.put(key, columnValues);
    }

    @Override
    public void removeEntity(final EntityKey key, final EntityEntry entityEntry) {
        removeEntity(key);
        addEntityEntry(key, entityEntry);
    }

    private void removeEntity(final EntityKey key) {
        entitiesByKey.remove(key);
        entitySnapshotsByKey.remove(key);
    }

    @Override
    public boolean isDirty(final EntityKey key, final Object entity) {
        if (entitySnapshotsByKey.containsKey(key)) {
            final Columns columnValues = createColumnValues(entity);
            return !entitySnapshotsByKey.get(key).equals(columnValues);
        }

        return false;
    }

    @Override
    public EntityEntry getEntityEntry(final EntityKey key) {
        return entityEntryByKey.get(key);
    }

    private void addEntityEntry(final EntityKey key, final EntityEntry entityEntry) {
        entityEntryByKey.put(key, entityEntry);
    }

    @Override
    public boolean contains(final EntityKey key) {
        return entitiesByKey.containsKey(key);
    }
}
