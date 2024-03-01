package persistence.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EntitySnapshotDifference {
    private final Map<String, Object> difference = new HashMap<>();

    public EntitySnapshotDifference(EntitySnapshot entitySnapshot,
                                    EntitySnapshot newEntitySnapshot) {
        for (String key : newEntitySnapshot.keys()) {
            Object oldValue = entitySnapshot.getValue(key);
            Object newValue = newEntitySnapshot.getValue(key);

            if (isDiffer(oldValue, newValue)) {
                difference.put(key, newValue);
            }
        }
    }

    private boolean isDiffer(Object oldValue, Object newValue) {
        if (oldValue == null) {
            return newValue != null;
        }
        return !oldValue.equals(newValue);
    }

    public boolean isDirty() {
        return !difference.isEmpty();
    }

    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(difference);
    }
}
