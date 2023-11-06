package persistence.entity;

import java.util.Optional;
import persistence.entity.impl.SnapShot;

public interface PersistenceContext {
    Optional<Object> getEntity(Class<?> entityClazz, Object id);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    Object getDatabaseSnapshot(Object id, Object entity);

    SnapShot getSnapShot(Class<?> entityClazz, Object id);

    void purgeEntityCache(Object entity);

    void clearContextCache();
}
