package persistence.entity;

import java.util.Optional;

public interface EntityManager {
    <T> Optional<T> find(Class<T> clazz, Object id);

    void persist(Object entity);

    void remove(Object entity);
}
