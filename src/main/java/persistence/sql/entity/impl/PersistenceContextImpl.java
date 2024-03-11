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
    public Object getEntity(final EntityKey id) {
        return entitiesByKey.get(id);
    }

    @Override
    public void addEntity(final EntityKey key, final Object entity) {
        entitiesByKey.put(key, entity);

        final Columns columnValues = createColumnValues(entity);
        entitySnapshotsByKey.put(key, columnValues);
    }

    @Override
    public void removeEntity(final EntityKey key) {
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
    public EntityEntry getEntityEntry(final EntityKey id) {
        return entityEntryByKey.get(id);
    }
}
