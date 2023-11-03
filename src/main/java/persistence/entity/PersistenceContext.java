package persistence.entity;

public interface PersistenceContext {

    Object getEntity(EntityKey entityKey);

    void addEntity(EntityKey entityKey, Object entity);

    void removeEntity(EntityKey entityKey);

}
