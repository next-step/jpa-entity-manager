package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityEntries {

    final Map<Object, EntityEntry> entityEntries = new HashMap<>();

    public void addOrChange(Object entity, Status status) {
        if (!entityEntries.containsKey(entity)) {
            entityEntries.put(entity, new EntityEntry(status));
            return;
        }

        entityEntries.get(entity).setStatus(status);
    }

    public Status getStatus(Object entity) {
        if (!entityEntries.containsKey(entity)) {
            return Status.NEW;
        }

        return entityEntries.get(entity).getStatus();
    }
}
