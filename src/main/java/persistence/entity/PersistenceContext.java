package persistence.entity;

import java.util.List;
import java.util.Queue;

public interface PersistenceContext {
    void addEntity(Object entry);

    <T> T getEntity(Class<T> entityType, Object id);

    void removeEntity(Object entity);

    <T> T getSnapshot(Class<T> entityType, Object id);

    void addToPersistQueue(Object entity);

    void addToRemoveQueue(Object entity);

    Queue<Object> getPersistQueue();

    Queue<Object> getRemoveQueue();

    List<Object> getAllEntity();

    EntityEntry getEntityEntry(Object entity);

    void createOrUpdateStatus(Object entity, EntityStatus entityStatus);
}
