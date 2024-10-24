package persistence.sql.simpleEntity;

public interface SimpleEntityManager<T, ID> {

    T findById(ID id);

    void persist(T entity) throws IllegalAccessException;

    void remove(T entity);

    void update(T entity) throws IllegalAccessException;

}
