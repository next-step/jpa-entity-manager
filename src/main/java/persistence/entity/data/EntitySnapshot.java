package persistence.entity.data;

import database.sql.util.EntityMetadata;
import database.sql.util.column.EntityColumn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EntitySnapshot {
    private final Map<String, Object> snapshot;

    public EntitySnapshot(Object entity) {
        snapshot = new HashMap<>();
        if (entity == null) {
            return;
        }

        EntityMetadata entityMetadata = new EntityMetadata(entity.getClass());
        for (EntityColumn column : entityMetadata.getGeneralColumns()) {
            String key = column.getColumnName();
            Object value = column.getValue(entity);
            snapshot.put(key, value);
        }
    }

    public Object getValue(String columnName) {
        return snapshot.get(columnName);
    }

    public Set<String> keys() {
        return snapshot.keySet();
    }
}
