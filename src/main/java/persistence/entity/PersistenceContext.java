package persistence.entity;

import java.util.Optional;

public interface PersistenceContext {

    <T, ID> Optional<T> getEntity(ID id, Class<T> entityType);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    void addDatabaseSnapshot(Object entity);

    <T, ID> EntitySnapshot getDatabaseSnapshot(ID id, Class<T> entityType);

    <T, ID> EntityEntry getEntry(ID id, Class<T> entityType);

    EntityEntry addEntry(Object entity, EntityStatus status);

    void updateEntry(Object entity, EntityStatus status);

}
