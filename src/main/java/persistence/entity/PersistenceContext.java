package persistence.entity;

import java.util.Optional;

public interface PersistenceContext {
    Optional<Object> getEntity(EntityKey key);

    void addEntity(EntityKey key, Object entity);

    void removeEntity(EntityKey key);

    boolean hasEntity(EntityKey key);

    Object getDatabaseSnapshot(EntityKey key, Object entity);

    void addEntityEntry(Object entity, Status status);

    Optional<EntityEntry> getEntityEntry(Object entity);

    void updateEntityEntryStatus(Object entity, Status status);
}
