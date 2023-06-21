package persistence.entity;

public interface PersistenceContext {
    Object removeEntity(EntityKey key);

    Object getEntity(EntityKey key);

    boolean containsEntity(EntityKey key);

    void addEntity(EntityKey key, Object entity);
}
