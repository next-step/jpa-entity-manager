package persistence.entity;

public interface EntityManager {

    <T> T find(Class<T> clazz, Object primaryKet);

    void persist(Object entity);

    void remove(Object entity);

}
