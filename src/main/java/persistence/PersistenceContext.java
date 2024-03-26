package persistence;

import persistence.entity.Status;

public interface PersistenceContext<T> {

    T getEntity(Long id);

    void addEntityEntry(T entity, Status status);

    void addEntityEntry(Long id, T entity);

    T removeEntity(Long id);

    T getCachedDatabaseSnapshot(Long id, T entity);
}
