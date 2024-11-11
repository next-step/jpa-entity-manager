package persistence.entity;

import java.util.Optional;

public interface PersistenceContext {

    <T, ID> Optional<T> getEntity(ID id, Class<T> entityType);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    void addDatabaseSnapshot(Object entity);

    <T, ID> EntitySnapshot getDatabaseSnapshot(ID id, Class<T> entityType);

    EntityEntry addEntityEntry(Object entity, EntityStatus status);

    void updateEntityEntry(Object entity, EntityStatus status);

}
