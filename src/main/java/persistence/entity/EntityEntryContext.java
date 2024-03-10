package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityEntryContext {
    private final Map<EntityKey, EntityEntry> entriesByKey = new HashMap<>();

    public EntityEntry getEntry(EntityKey id) {
        return entriesByKey.get(id);
    }

    public void addEntry(EntityKey id, EntityEntry entry) {
        entriesByKey.put(id, entry);
    }
}
