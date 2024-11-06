package persistence.sql.simpleEntity;

import java.util.List;

public interface SimpleEntityManager<T, ID> {

    T findById(ID id);

    void persist(T entity) throws IllegalAccessException;

    void remove(T entity);

    void update(T entity, List<String> changedColumns) throws IllegalAccessException;

}
