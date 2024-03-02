package persistence.entity;

import persistence.jpa.Cache;
import persistence.jpa.SnapShot;

import java.util.Optional;

public class HibernatePersistContext implements PersistenceContext {

    private final Cache cache;
    private final SnapShot snapshot;

    public HibernatePersistContext() {
        this.cache = new Cache();
        this.snapshot = new SnapShot();
    }

    @Override
    public <T> Optional<T> getEntity(Class<T> clazz, Object id) {
        return (Optional<T>) cache.get(new EntityKey(clazz, id));
    }

    @Override
    public void addEntity(Object entity, Object id) {
        cache.save(new EntityKey(entity.getClass(), id), entity);
    }

    @Override
    public void removeEntity(Class<?> clazz, Object id) {
        cache.remove(new EntityKey(clazz, id));
    }

    @Override
    public void getDatabaseSnapshot(EntityMetaData entity, Object id) {
        snapshot.save(new EntityKey(entity.getClazz(), id), entity);
    }

    @Override
    public EntityMetaData getCachedDatabaseSnapshot(Class<?> clazz, Object id) {
        return snapshot.get(new EntityKey(clazz, id));
    }
}
