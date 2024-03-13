package persistence.entity.persistence;

import persistence.entity.domain.EntityEntry;
import persistence.entity.domain.EntitySnapshot;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, Object id);

    void addEntity(Object id, Object entity);

    void removeEntity(Object entity);

    EntitySnapshot getDatabaseSnapshot(Object id, Object entity);

    EntitySnapshot getCachedDatabaseSnapshot(Object id, Object entity);

    void addEntityEntry(Object entity, EntityEntry entityEntry);

    EntityEntry getEntityEntry(Object entity);

}
