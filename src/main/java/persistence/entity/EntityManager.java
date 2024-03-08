package persistence.entity;

public interface EntityManager {

    <T> T find(Class<T> clazz, EntityId id);

    void persist(Object entity);

    void merge(Object entity);

    void remove(Object entity);
}
