package persistence.entity;

import persistence.entity.entry.EntityEntryStatus;

import java.util.List;

public interface PersistenceContext {
    <T> T getEntity(Class<T> entityClass, Object id);

    void addEntry(Object entityObject, EntityEntryStatus entryStatus);

    void addEntry(Class<?> entityClass, Object id, EntityEntryStatus entryStatus);

    void updateEntry(Object entityObject, EntityEntryStatus entryStatus);

    void addEntity(Object entityObject);

    void removeEntity(Object entityObject);

    void updateEntity(Object entityObject);

    boolean isEntityExists(Object entityObject);

    EntitySnapshot getSnapshot(Object entityObject);

    List<EntitySnapshot> getDirtySnapshots();
}
