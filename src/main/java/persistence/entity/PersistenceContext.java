package persistence.entity;

import java.util.List;

public interface PersistenceContext {
    <T> T getEntity(Class<T> entityClass, Object id);

    void addEntity(Object entityObject);

    void removeEntity(Object entityObject);

    void updateEntity(Object entityObject);

    boolean isEntityExists(Object entityObject);

    EntitySnapshot getSnapshot(Object entityObject);

    List<EntitySnapshot> getDirtySnapshots();
}
