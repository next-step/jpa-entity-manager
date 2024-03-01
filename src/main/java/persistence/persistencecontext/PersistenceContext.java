package persistence.persistencecontext;

import persistence.entity.EntityStatus;

import java.util.List;
import java.util.Optional;

public interface PersistenceContext {
    Optional<Object> getEntity(Class<?> clazz, Object id);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    Object getDatabaseSnapshot(Object entity);

    Object getCachedDatabaseSnapshot(Object entity);

    void addEntityEntry(Object entity, EntityStatus entityStatus);

    List<Object> getDirtyEntities();
}
