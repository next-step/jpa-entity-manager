package persistence.context;

import java.util.Collection;

public interface PersistenceContext {

    <T, ID> T get(Class<T> entityType, ID id);

    <T> Collection<T> getAll(Class<T> entityType);

    <T, ID> T add(ID id, T entity);
}
