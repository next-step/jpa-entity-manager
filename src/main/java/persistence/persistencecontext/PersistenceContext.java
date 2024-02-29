package persistence.persistencecontext;

import persistence.entity.EntityMeta;

import java.util.Optional;

public interface PersistenceContext {
    <T> Optional<T> getEntity(Class<T> clazz, Object id);

    void addEntity(EntityMeta entityMeta);

    void removeEntity(EntityMeta entity);

    Object getDatabaseSnapshot(EntityMeta entityMeta);

    Object getCachedDatabaseSnapshot(EntityMeta entityMeta);
}
