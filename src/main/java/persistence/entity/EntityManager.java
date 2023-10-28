package persistence.entity;

import java.util.List;

public interface EntityManager {
    <T> T persist(T entity);

    void remove(Object entity);

    <T, ID> T find(Class<T> clazz, ID id);

    <T> List<T> findAll(Class<T> tClass);

    void flush();
}
