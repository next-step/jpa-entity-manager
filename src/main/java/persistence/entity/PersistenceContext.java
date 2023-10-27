package persistence.entity;

import java.util.List;

public interface PersistenceContext {
    Object getEntity(Object id);

    void addEntity(Object id, Object entity);

    void removeEntity(Object entity);

    Object getDatabaseSnapshot(Object id, Object entity);

    Object getCachedDatabaseSnapshot(Object id);

    List<Object> getChangedEntity();

    void clear();
}
