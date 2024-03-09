package persistence.sql.entity.manager;

import java.util.List;

public interface EntityManager {

    <T> List<T> findAll(Class<T> clazz);

    <T> T find(Class<T> clazz, Object id);

    <T> T findOfReadOnly(Class<T> clazz, Object id);

    void persist(Object entity);

    void remove(Object entity);

    void removeAll(Class<?> clazz);
}
