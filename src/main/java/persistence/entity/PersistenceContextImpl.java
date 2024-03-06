package persistence.entity;

import persistence.sql.mapping.ColumnData;
import persistence.sql.mapping.Columns;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<Long, Object> entitiesByKey = new HashMap<>();
    private final Map<Long, Snapshot> snapshotsByKey = new HashMap<>();
    @Override
    public Object getEntity(Long id) {
        return entitiesByKey.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        entitiesByKey.put(id, entity);
        snapshotsByKey.put(id, new Snapshot(entity));
    }

    @Override
    public void removeEntity(Object entity) {
        Columns columns = Columns.createColumnsWithValue(entity);
        ColumnData keyColumn = columns.getKeyColumn();
        Long key = (Long) keyColumn.getValue();
        entitiesByKey.remove(key);
        snapshotsByKey.remove(key);
    }

    @Override
    public boolean isDirty(Long id, Object entity) {
        Snapshot snapshot = snapshotsByKey.get(id);
        return !snapshot.equals(new Snapshot(entity));
    }
}
