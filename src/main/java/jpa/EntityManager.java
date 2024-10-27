package jpa;

public interface EntityManager {

    <T> T find(Class<T> clazz, Long id);

    <T> T persist(T entity);

    void remove(Object entity);

    void update(Object entity);

    boolean isDirty(Object entity);

}
