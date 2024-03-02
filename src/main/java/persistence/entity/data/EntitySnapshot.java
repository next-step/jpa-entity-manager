package persistence.entity.data;

import database.sql.util.EntityMetadata;
import database.sql.util.column.EntityColumn;

import java.util.HashMap;
import java.util.Map;

public class EntitySnapshot {
    private final Map<String, Object> snapshot;

    private EntitySnapshot() {
        this.snapshot = new HashMap<>();
    }

    public static EntitySnapshot of(Object entity) {
        EntitySnapshot ret = new EntitySnapshot();

        if (entity == null) {
            return ret;
        }

        EntityMetadata entityMetadata = new EntityMetadata(entity.getClass());
        for (EntityColumn column : entityMetadata.getGeneralColumns()) {
            String key = column.getColumnName();
            Object value = column.getValue(entity);
            ret.snapshot.put(key, value);
        }
        return ret;
    }

    public Map<String, Object> changes(EntitySnapshot newEntitySnapshot) {
        Map<String, Object> changes = new HashMap<>();

        for (String key : newEntitySnapshot.snapshot.keySet()) {
            Object oldValue = snapshot.get(key);
            Object newValue = newEntitySnapshot.snapshot.get(key);

            if (isDiffer(oldValue, newValue)) {
                changes.put(key, newValue);
            }
        }
        return changes;
    }

    private boolean isDiffer(Object oldValue, Object newValue) {
        if (oldValue == null) {
            return newValue != null;
        }
        return !oldValue.equals(newValue);
    }
}
