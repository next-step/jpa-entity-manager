package persistence.entity.context;

import persistence.entity.context.cache.EntityKey;
import persistence.entity.context.cache.EntitySnapshots;
import persistence.entity.context.cache.PersistenceCache;

public class StatefulPersistenceContext implements PersistenceContext {

    private final PersistenceCache cache = new PersistenceCache();
    private final EntitySnapshots snapshots = new EntitySnapshots();

    @Override
    public <T> T getEntity(final Object key, final String className) {
        final EntityKey<T> entityKey = generateEntityKey(key, className);
        return this.cache.get(entityKey);
    }

    @Override
    public void addEntity(final Object key, final Object entity) {
        final EntityKey<?> entityKey = generateEntityKey(key, entity.getClass().getName());
        this.cache.add(entityKey, entity);
        this.snapshots.add(entityKey, entity);
    }

    @Override
    public void removeEntity(final Object key, final Object entity) {
        final EntityKey<?> entityKey = generateEntityKey(key, entity.getClass().getName());
        this.cache.remove(entityKey);
        this.snapshots.remove(entityKey);
    }

    @Override
    public boolean checkDirty(final Object key, final Object entity) {
        final EntityKey<?> entityKey = generateEntityKey(key, entity.getClass().getName());
        return !this.snapshots.compareWithSnapshot(entityKey, entity);
    }

    private <T> EntityKey<T> generateEntityKey(final Object key, final String className) {
        return new EntityKey<T>(key, className);
    }
}
