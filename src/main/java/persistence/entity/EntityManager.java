package persistence.entity;

import java.util.Optional;

public interface EntityManager {
    <T> Optional<T> find(Class<T> clazz, Object id);

    void persist(Object entity);

    void remove(Object entity);

    void merge(Object entity);

    void detach(Object entity);

    boolean isDirty(Object entity);
}
