package persistence.sql.entity.persister;

public interface EntityPersister<T> {

    boolean update(T entity);

    void insert(T entity);

    Object insertWithPk(T entity);

    void delete(T entity);

    void deleteAll();

}
