package persistence.entity;

public interface PersistenceContext {
    Object getEntity(EntityKey id);

    void addEntity(EntityKey id, Object entity);

    void removeEntity(Object entity);

    Object getDatabaseSnapshot(EntityKey id, Object entity);

}
