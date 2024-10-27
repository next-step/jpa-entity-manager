package persistence.sql.context;

import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;

public interface PersistenceContext {

    <T> EntityEntry addEntry(T entity, Status status, EntityPersister entityPersister);

    <T, ID> EntityEntry getEntry(Class<T> entityType, ID id);

    <T, ID> void deleteEntry(T entity, ID id);

    void cleanup();

    void dirtyCheck();
}
