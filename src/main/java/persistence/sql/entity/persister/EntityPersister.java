package persistence.sql.entity.persister;

public interface EntityPersister<T, K> {

    boolean update(T entity);

    void insert(T entity);

    void delete(K key);

}
