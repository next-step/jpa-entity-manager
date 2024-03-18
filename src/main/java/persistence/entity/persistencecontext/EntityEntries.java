package persistence.entity.persistencecontext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityEntries {
    private final Map<EntityKey, EntityEntry> entityEntries;

    public EntityEntries() {
        this.entityEntries = new HashMap<>();
    }

    public Optional<EntityEntry> get(Class<?> clazz, Long id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        EntityEntry entityEntry = entityEntries.get(entityKey);
        if (entityEntry == null) {
            return Optional.empty();
        }
        return Optional.of(entityEntry);
    }

    public Optional<EntityEntry> get(EntityKey entityKey) {
        EntityEntry entityEntry = entityEntries.get(entityKey);
        if (entityEntry == null) {
            return Optional.empty();
        }
        return Optional.of(entityEntry);
    }

    public <T> void put(T entity) {
        EntityKey key = new EntityKey(entity);
        if (key.getId() == null) {
            return;
        }
        entityEntries.put(key, new EntityEntry());
    }
}
