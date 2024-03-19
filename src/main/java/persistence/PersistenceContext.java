package persistence;

public interface PersistenceContext {

    Object getEntity(Long id);

    void addEntity(Long id, Object entity);

    Object removeEntity(Long id);

    Object getCachedDatabaseSnapshot(Long id, Object entity);
}
