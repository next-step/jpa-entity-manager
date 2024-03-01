package persistence.entity.persistencecontext;

public interface PersistenceContext {

    Object getEntity(Class<?> clazz, Object id);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    EntitySnapshot getCachedDatabaseSnapshot(Object entity);

    EntitySnapshot getDatabaseSnapshot(Object entity);
}
