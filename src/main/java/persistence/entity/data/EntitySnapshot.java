package persistence.entity.data;

import database.mapping.EntityMetadata;
import database.mapping.column.EntityColumn;

import java.util.HashMap;
import java.util.Map;

public class EntitySnapshot {
    private final Map<String, Object> snapshot;

    private EntitySnapshot() {
        this.snapshot = new HashMap<>();
    }

    public static EntitySnapshot of(Object entity) {
        EntitySnapshot newSnapshot = new EntitySnapshot();

        if (entity == null) {
            return newSnapshot;
        }

        EntityMetadata entityMetadata = EntityMetadata.fromClass(entity.getClass());
        for (EntityColumn column : entityMetadata.getGeneralColumns()) {
            String key = column.getColumnName();
            Object value = column.getValue(entity);
            newSnapshot.snapshot.put(key, value);
        }
        return newSnapshot;
    }

    public Map<String, Object> diff(EntitySnapshot newEntitySnapshot) {
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
