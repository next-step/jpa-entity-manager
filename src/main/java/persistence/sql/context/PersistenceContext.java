package persistence.sql.context;

public interface PersistenceContext {

    <T, ID> T get(Class<T> entityType, ID id);

    <T, ID> void add(ID id, T entity);

    <T> void delete(T entity);

    <T, ID> T getDatabaseSnapshot(ID id, T entity);
}
