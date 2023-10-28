package persistence.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityCache {
    private final Map<EntityKey, Object> entities;

    public EntityCache() {
        entities = new HashMap<>();
    }

    public Optional<Object> get(final EntityKey key) {
        return Optional.ofNullable(entities.get(key));
    }

    public void add(final EntityKey key, final Object entity) {
        entities.put(key, entity);
    }

    public void remove(final EntityKey key) {
        entities.remove(key);
    }

    public boolean containsKey(final EntityKey key) {
        return entities.containsKey(key);
    }

}
