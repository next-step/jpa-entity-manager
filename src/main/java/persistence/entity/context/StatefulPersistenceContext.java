package persistence.entity.context;

import persistence.entity.context.cache.EntityKey;
import persistence.entity.context.cache.PersistenceCache;

public class StatefulPersistenceContext implements PersistenceContext {

    private final PersistenceCache cache = new PersistenceCache();

    @Override
    public <T> T getEntity(final Object key, final String className) {
        final EntityKey<T> entityKey = generateEntityKey(key, className);
        return this.cache.get(entityKey);
    }

    @Override
    public void addEntity(final Object key, final Object entity) {
        final EntityKey<?> entityKey = generateEntityKey(key, entity.getClass().getName());
        this.cache.add(entityKey, entity);
    }

    @Override
    public void removeEntity(final Object key, final Object entity) {
        final EntityKey<?> entityKey = generateEntityKey(key, entity.getClass().getName());
        this.cache.remove(entityKey);
    }

    private <T> EntityKey<T> generateEntityKey(final Object key, final String className) {
        return new EntityKey<T>(key, className);
    }
}
