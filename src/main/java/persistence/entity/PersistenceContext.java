package persistence.entity;

import java.util.List;

public interface PersistenceContext {

    <T> T getEntity(Class<T> entityClass, Object primaryKey);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    void detachEntity(Object entity);

    List<Object> getSavingEntities();

    List<Object> getDeletedEntities();

    List<Object> getManagedEntities();

    List<String> findDirtyColumns(Object entity) throws IllegalAccessException;

    void updateDatabaseSnapshot(Object entity);

    void mangeEntity(Object entity) throws IllegalAccessException;

}
