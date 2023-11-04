package persistence.entity;

public interface PersistenceContext {
    Object getEntity(EntityPersistIdentity id);

    void addEntity(EntityPersistIdentity id, Object entity);

    void removeEntity(Object entity);
}
