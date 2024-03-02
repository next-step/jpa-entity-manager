package persistence.entity.persistencecontext;

import persistence.entity.EntityEntry;

public interface PersistenceContext {

    Object getEntity(Class<?> clazz, Object id);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    EntitySnapshot getCachedDatabaseSnapshot(Object entity);

    EntitySnapshot getDatabaseSnapshot(Object entity);

    void setEntityEntry(Object entity, EntityEntry entityEntry);

    EntityEntry getEntityEntry(Object entity);
}
