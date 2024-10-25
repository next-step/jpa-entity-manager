package persistence.entity;

import java.util.Collection;
import java.util.Set;

public interface PersistenceContext {

    <T> T find(Class<T> entityClass, Object primaryKey);

    void persist(Object entity);

    void remove(Object entity);

    void update(Object entity) throws IllegalAccessException;

    Set<Object> getPendingEntities();

    Collection<Object> getPersistedEntities();

}
