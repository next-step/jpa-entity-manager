package persistence.entity;

public interface PersistenceContext {
    boolean hasEntity(EntityKey key);

    <T> T getEntity(EntityKey<T> key);

    void addEntity(Object entity);

    void removeEntity(Object entity);
}
