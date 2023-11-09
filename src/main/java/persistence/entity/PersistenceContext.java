package persistence.entity;

public interface PersistenceContext {

    Object getEntity(EntityKey entityKey);

    void addEntity(EntityKey entityKey, Object entity);

    void removeEntity(EntityKey entityKey);

    Object getDatabaseSnapshot(EntityKey entityKey, Object entity);

    void addEntityEntry(Object object, EntityEntry entityEntry);

    EntityEntry getEntityEntry(Object object);

    void deleteEntityEntry(Object object);

    void updateEntityEntryStatus(Object object, Status status);

}
