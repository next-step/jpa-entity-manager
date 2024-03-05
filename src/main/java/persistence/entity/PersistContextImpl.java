package persistence.entity;

import persistence.sql.mapping.ColumnData;
import persistence.sql.mapping.Columns;

import java.util.HashMap;
import java.util.Map;

public class PersistContextImpl implements PersistenceContext {
    private final Map<Long, Object> cache = new HashMap<>();
    @Override
    public Object getEntity(Long id) {
        return cache.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        cache.put(id, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        Columns columns = Columns.createColumnsWithValue(entity);
        ColumnData keyColumn = columns.getKeyColumn();
        cache.remove(keyColumn.getValue());
    }
}
