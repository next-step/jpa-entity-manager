package persistence.entity;

import persistence.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entities;
    private final Map<EntityKey, Object> entitySnapshots;

    public SimplePersistenceContext() {
        this.entities = new HashMap<>();
        this.entitySnapshots = new HashMap<>();
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

    @Override
    public boolean hasEntity(final EntityKey key) {
        return entities.containsKey(key);
    }

    @Override
    public Object getDatabaseSnapshot(final EntityKey key, final Object entity) {
        return entitySnapshots.computeIfAbsent(key, entityKey -> ReflectionUtils.shallowCopy(entity));
    }

}
