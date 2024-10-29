package persistence.entity;

import java.util.Collection;
import java.util.Set;

public interface PersistenceContext {

    <T> T getEntity(Class<T> entityClass, Object primaryKey);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    void update(Object entity) throws IllegalAccessException;

    Set<Object> getPendingEntities();

    Collection<Object> getPersistedEntities();

    int getDatabaseSnapshot(Object entity);

}
