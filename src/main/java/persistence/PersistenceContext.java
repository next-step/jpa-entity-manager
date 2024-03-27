package persistence;

import persistence.entity.Status;

public interface PersistenceContext<T> {

    T getEntity(Long id);

    void addEntityEntry(T entity, Status status);

    void addEntity(Long id, T entity);

    T removeEntity(T entity);

    T getCachedDatabaseSnapshot(Long id, T entity);
}
