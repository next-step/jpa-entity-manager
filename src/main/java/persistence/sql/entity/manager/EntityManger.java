package persistence.sql.entity.manager;

import java.util.List;

public interface EntityManger<T> {

    List<T> findAll(Class<T> clazz);

    T find(Class<T> clazz, Object id);

    void persist(T entity);

    void remove(T entity);

    void removeAll(Class<T> clazz);

}
