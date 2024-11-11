package persistence.entity;

import java.util.Optional;

public interface PersistenceContext {

    <T, ID> Optional<T> getEntity(ID id, Class<T> entityType);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    void addDatabaseSnapshot(Object entity);

    <T> EntitySnapshot getDatabaseSnapshot(T entity);

    void addEntityEntry(Object entity, EntityStatus status);

    void updateEntityEntry(Object entity, EntityStatus status);

    <T> boolean isDirty(T entity);

}
