package persistence.sql.entity.impl;

import persistence.sql.entity.PersistenceContext;
import persistence.sql.meta.Columns;
import persistence.sql.meta.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

import static persistence.sql.meta.simple.SimpleEntityMetaCreator.createColumnValues;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<PrimaryKey, Object> entitiesByKey = new HashMap<>();
    private final Map<PrimaryKey, Columns> entitySnapshotsByKey = new HashMap<>();

    @Override
    public Object getEntity(final PrimaryKey id) {
        return entitiesByKey.get(id);
    }

    @Override
    public void addEntity(final PrimaryKey id, final Object entity) {
        entitiesByKey.put(id, entity);

        final Columns columnValues = createColumnValues(entity);
        entitySnapshotsByKey.put(id, columnValues);
    }

    @Override
    public void removeEntity(final PrimaryKey id) {
        entitiesByKey.remove(id);
        entitySnapshotsByKey.remove(id);
    }

    @Override
    public boolean isDirty(final PrimaryKey id, final Object entity) {
        if (entitySnapshotsByKey.containsKey(id)) {
            final Columns columnValues = createColumnValues(entity);
            return !entitySnapshotsByKey.get(id).equals(columnValues);
        }

        return false;
    }
}
