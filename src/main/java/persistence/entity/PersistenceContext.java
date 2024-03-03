package persistence.entity;

import java.util.Optional;
import java.util.Queue;

import persistence.entity.event.DeleteEvent;
import persistence.entity.event.UpdateEvent;

public interface PersistenceContext {

    <T> Optional<T> getEntity(Class<T> clazz, Object id);

    void addEntity(Object entity, Object id);

    void removeEntity(Class<?> clazz, Object id);

    EntityMetaData getDatabaseSnapshot(EntityMetaData entityMetaData, Object id);

    Queue<DeleteEvent> getDeleteActionQueue();

    Queue<UpdateEvent> getUpdateActionQueue();

    void addDeleteActionQueue(DeleteEvent event);

    void addUpdateActionQueue(UpdateEvent event);

    void updateEntityEntryToGone(Object entity, Object id);

    <T> EntityMetaData getSnapshot(T entity, Object id);
}
