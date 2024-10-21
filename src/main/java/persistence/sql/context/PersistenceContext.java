package persistence.sql.context;

import java.util.List;

public interface PersistenceContext {

    <T, ID> T get(Class<T> entityType, ID id);

    <T> List<T> getAll(Class<T> entityType);

    <T, ID> T add(ID id, T entity);

    <T, ID> void merge(ID id, T entity);

    <T> void delete(T entity);
}
