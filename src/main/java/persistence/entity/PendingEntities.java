package persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingEntities {

    private final Map<Integer, EntityEntry> entityEntries = new HashMap<>();

    public void persistEntity(Object entity) {
        entityEntries.put(getKey(entity), new EntityEntry(entity, Status.SAVING));
    }

    public List<Object> getEntities() {
        return entityEntries.values().stream().map(EntityEntry::getEntity).toList();
    }

    public void evict(Object entity) {
        entityEntries.remove(getKey(entity));
    }

    private int getKey(Object entity) {
        return System.identityHashCode(entity);
    }

}
