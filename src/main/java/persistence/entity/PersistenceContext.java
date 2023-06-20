package persistence.entity;

public interface PersistenceContext {
    boolean hasEntity(EntityKey key);

    <T> T findEntity(EntityKey<T> key);

    void persistEntity(Object object);

    void removeEntity(Object object);
}
