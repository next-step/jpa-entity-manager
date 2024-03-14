package persistence.entity.context;

import persistence.entity.context.cache.EntityKey;
import persistence.entity.context.cache.EntitySnapshot;
import persistence.entity.context.cache.EntitySnapshots;
import persistence.entity.context.cache.PersistenceCache;

public class StatefulPersistenceContext implements PersistenceContext {

    private final PersistenceCache cache = new PersistenceCache();
    private final EntitySnapshots snapshots = new EntitySnapshots();
    private final EntityEntryContext entityEntryContext = new EntityEntryContext();

    @Override
    public <T> T getEntity(final Object key, final String className) {
        final EntityKey entityKey = generateEntityKey(key, className);
        return this.cache.get(entityKey);
    }

    @Override
    public void addEntity(final Object key, final Object entity) {
        final EntityKey entityKey = generateEntityKey(key, entity.getClass().getName());
        this.cache.add(entityKey, entity);
        this.snapshots.add(entityKey, entity);
        this.entityEntryContext.add(entityKey);
    }

    @Override
    public void removeEntity(final Object key, final Object entity) {
        final EntityKey entityKey = generateEntityKey(key, entity.getClass().getName());
        this.cache.remove(entityKey);
        this.snapshots.remove(entityKey);
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(final Object key, final Object entity) {
        final EntityKey entityKey = generateEntityKey(key, entity.getClass().getName());
        return this.snapshots.get(entityKey, entity);
    }

    @Override
    public EntityEntry getEntityEntry(final Object key, final Class<?> entityClass) {
        final EntityKey entityKey = generateEntityKey(key, entityClass.getName());
        return this.entityEntryContext.get(entityKey);
    }

    @Override
    public EntityEntry getEntityEntry(final Object key, final Object entity) {
        return this.getEntityEntry(key, entity.getClass());
    }

    private <T> EntityKey generateEntityKey(final Object key, final String className) {
        return new EntityKey(key, className);
    }
}
