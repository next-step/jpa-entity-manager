package persistence.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entities;

    public SimplePersistenceContext() {
        this.entities = new HashMap<>();
    }

    @Override
    public Optional<Object> getEntity(final EntityKey key) {
        return Optional.ofNullable(entities.get(key));
    }

    @Override
    public void addEntity(final EntityKey key, final Object entity) {
        entities.put(key, entity);
    }

    @Override
    public void removeEntity(final EntityKey key) {
        entities.remove(key);
    }

}
