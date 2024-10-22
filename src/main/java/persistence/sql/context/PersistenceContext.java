package persistence.sql.context;

public interface PersistenceContext {

    <T, ID> T get(Class<T> entityType, ID id);

    <T, ID> void add(ID id, T entity);

    <T, ID> void merge(ID id, T entity);

    <T> void delete(T entity);
}
