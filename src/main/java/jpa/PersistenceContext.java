package jpa;

import java.util.List;

public interface PersistenceContext {

    void add(Object entity);

    <T> T get(Class<T> clazz, Long id);

    void remove(Object entity);

    <T> T getDatabaseSnapshot(T entity);

    void createDatabaseSnapshot(Object entity);

    void removeDatabaseSnapshot(Object entity);

    boolean isDirty(Object entity);

    List<Object> getDirtyEntities();

    void addEntityEntry(Object entity, EntityEntry entityEntry);

    EntityEntry getEntityEntry(Object entity);

}
