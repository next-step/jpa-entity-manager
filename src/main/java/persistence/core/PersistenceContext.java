package persistence.core;

import java.util.List;

public interface PersistenceContext {

    EntityKey getEntityKey(Class<?> clazz, Long id);

    Object getEntity(EntityKey entityKey);

    void addEntity(EntityKey entityKey, Object entity);

    void removeEntity(EntityKey entityKey);

    void getDatabaseSnapshot(EntityKey entityKey);

    <T> List<T> dirtyCheck();

    void addEntityEntry(EntityKey entityKey, EntityEntry entityEntry);

    EntityEntry getEntityEntry(EntityKey entityKey);

    void clear();
}
