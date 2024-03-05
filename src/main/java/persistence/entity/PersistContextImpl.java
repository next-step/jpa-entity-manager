package persistence.entity;

import persistence.sql.mapping.ColumnData;
import persistence.sql.mapping.Columns;

import java.util.HashMap;
import java.util.Map;

public class PersistContextImpl implements PersistenceContext {
    private final Map<Long, Object> entitiesByKey = new HashMap<>();
    private final Map<Long, Object> snapshotsByKey = new HashMap<>();
    @Override
    public Object getEntity(Long id) {
        return entitiesByKey.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        entitiesByKey.put(id, entity);
        snapshotsByKey.put(id, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        Columns columns = Columns.createColumnsWithValue(entity);
        ColumnData keyColumn = columns.getKeyColumn();
        entitiesByKey.remove(keyColumn.getValue());
    }
}
