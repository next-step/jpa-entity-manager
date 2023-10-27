package hibernate.entity.persistencecontext;

public interface PersistenceContext {

    Object getEntity(EntityKey id);

    void addEntity(Object id, Object entity);

    void removeEntity(Object entity);

    Object getDatabaseSnapshot(Object id, Object entity);

    EntitySnapshot getCachedDatabaseSnapshot(EntityKey id);
}
