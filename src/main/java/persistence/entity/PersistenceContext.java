package persistence.entity;

public interface PersistenceContext {

    Object getEntity(EntityKey entityKey);

    EntitySnapshot getDatabaseSnapshot(EntityKey entityKey, Object entity);

    void addEntity(EntityKey entityKey, Object entity);

    void addDatabaseSnapshot(EntityKey entityKey, Object entity);

    void removeEntity(EntityKey entityKey);

    boolean isEntityAbsent(Object entity, Object id);
}
