package persistence;

public interface PersistenceContext<T> {

    Object getEntity(Long id);

    void addEntity(Long id, Object entity);

    Object removeEntity(Long id);

    T getCachedDatabaseSnapshot(Long id, T entity);
}
