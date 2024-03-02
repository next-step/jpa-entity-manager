package persistence.entity;

import java.util.Optional;

public interface PersistenceContext {

    <T> Optional<T> getEntity(Class<T> clazz, Object id);

    void addEntity(Object entity, Object id);

    void removeEntity(Class<?> clazz, Object id);

    void getDatabaseSnapshot(EntityMetaData entityMetaData, Object id);

    EntityMetaData getCachedDatabaseSnapshot(Class<?> clazz, Object id);

}
