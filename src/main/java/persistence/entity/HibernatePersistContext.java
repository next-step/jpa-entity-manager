package persistence.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HibernatePersistContext implements PersistenceContext {

    private final Map<Long, Object> cache;
    private final Map<Long, Object> snapshot;

    public HibernatePersistContext() {
        this.cache = new HashMap<>();
        this.snapshot = new HashMap<>();
    }

    @Override
    public Optional<Object> getEntity(Long id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public void addEntity(Long id, Object entity) {
        cache.put(id, entity);
    }

    @Override
    public void removeEntity(Long id) {
        cache.remove(id);
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        return snapshot.put(id, entity);
    }

    @Override
    public <T> T getCachedDatabaseSnapshot(Long id) {
        return (T) snapshot.get(id);
    }
}
