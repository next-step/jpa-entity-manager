package persistence.jpa;

import persistence.Person;
import persistence.entity.EntityKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cache {

    private final Map<EntityKey, Object> cache;

    public Cache() {
        this.cache = new HashMap<>();
    }

    public Optional<Object> get(EntityKey entityKey) {
        return Optional.ofNullable(cache.get(entityKey));
    }

    public void save(EntityKey entityKey, Object entity) {
        cache.put(entityKey, entity);
    }

    public void remove(EntityKey entityKey) {
        cache.remove(entityKey);
    }
}
