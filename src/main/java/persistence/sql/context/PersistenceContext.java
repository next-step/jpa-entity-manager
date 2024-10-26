package persistence.sql.context;

import java.util.List;

public interface PersistenceContext {

    <T, ID> T get(Class<T> entityType, ID id);

    <T, ID> void add(ID id, T entity);

    <T, ID> void delete(T entity, ID id);

    <T, ID> T getDatabaseSnapshot(ID id, T entity);

    <T, ID> void createDatabaseSnapshot(ID id, T entity);

    boolean isDirty();

    List<Object> getDirtyEntities();

    void cleanup();

    <T> void updateSnapshot(Object id, T entity);
}
