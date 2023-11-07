package persistence.entity.entitymanager;

public interface EntityManager {
    <T> T find(Class<T> clazz, Long Id);

    void persist(Object entity);
    void merge(Object entity);

    void remove(Object entity);
}
