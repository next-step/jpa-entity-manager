package persistence.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityEntryContext {

    private final Map<Object, EntityEntry> context = new ConcurrentHashMap<>();

    public EntityEntry get(Object entity) {
        EntityEntry cachedEntry = context.get(entity);
        if (cachedEntry == null) {
            SimpleEntityEntry newEntry = new SimpleEntityEntry();
            context.put(entity, newEntry);
            return newEntry;
        }
        return cachedEntry;
    }

    public <T> void add(Object entity, EntityEntry entry) {
        context.put(entity, entry);
    }

}
