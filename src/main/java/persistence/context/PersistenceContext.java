package persistence.context;

import persistence.entity.EntitySnapshot;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, Object id);

    void addEntity(Object entity);

    void addEntity(Long id, Object entity);

    void removeEntity(Object entity);

    EntitySnapshot getDatabaseSnapshot(Object entity);

    EntitySnapshot getDatabaseSnapshot(Long id, Object entity);

    EntitySnapshot getCachedDatabaseSnapshot(Object entity);

    EntitySnapshot getCachedDatabaseSnapshot(Long id, Object entity);
}
