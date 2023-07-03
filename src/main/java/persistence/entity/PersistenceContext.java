package persistence.entity;

public interface PersistenceContext {

    Object getEntity(Long id);

    void addEntity(Long id, Object entity);

    void removeEntity(Long id);

    Object getDatabaseSnapshot(Long id, Object entity);

    Object getCachedDatabaseSnapshot(Long id);
}
