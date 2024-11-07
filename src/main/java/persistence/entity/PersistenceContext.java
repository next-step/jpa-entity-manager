package persistence.entity;

import java.util.Collection;
import java.util.Set;

public interface PersistenceContext {

    <T> T getEntity(Class<T> entityClass, Object primaryKey);

    void attachEntity(Object entity);

    void detachEntity(Object entity);

    Set<Object> getPendingEntities();

    Collection<Object> getPersistedEntities();

    void captureDatabaseSnapshot(Object entity);

    Object getDatabaseSnapshot(Object entity);

    void reset();

}
