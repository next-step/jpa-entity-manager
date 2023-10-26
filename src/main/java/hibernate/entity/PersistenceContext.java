package hibernate.entity;

public interface PersistenceContext {

    Object getEntity(EntityKey id);

    void addEntity(Object id, Object entity);

    void removeEntity(Object entity);

    Object getDatabaseSnapshot(Object id, Object entity);

    Object getCachedDatabaseSnapshot(EntityKey id);
}
