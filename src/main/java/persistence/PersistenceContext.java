package persistence;

public interface PersistenceContext<T> {

    T getEntity(Long id);

    void addEntity(Long id, T entity);

    T removeEntity(Long id);

    T getCachedDatabaseSnapshot(Long id, T entity);
}
