package persistence.entity.context;

import persistence.entity.context.cache.EntityKey;

import java.util.HashMap;
import java.util.Map;

public class EntityEntryContext {

    private final Map<EntityKey, EntityEntry> entries = new HashMap<>();

    public void add(final EntityKey entityKey) {
        this.entries.put(entityKey, new EntityEntry());
    }

    public EntityEntry get(final EntityKey entityKey) {
        return this.entries.get(entityKey);
    }

}
