package persistence.entity.context;

import persistence.entity.context.cache.EntitySnapshot;

public interface PersistenceContext {

    <T> T getEntity(final Object key, final String className);

    void addEntity(final Object key, final Object entity);

    void removeEntity(final Object key, final Object entity);

    EntitySnapshot getDatabaseSnapshot(final Object key, final Object entity);

    EntityEntry getEntityEntry(final Object key, final Class<?> entityClass);

    EntityEntry getEntityEntry(final Object key, final Object entity);
}
