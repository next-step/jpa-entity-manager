package persistence.core;

import java.util.List;

public interface PersistenceContext {
    Object getEntity(Class<?> entity, Long id);

    void addEntity(Long id, Object entity);

    void removeEntity(Long id, Class<?> clazz);

    void getDatabaseSnapshot(Long id, Object entity);

    <T> List<T> dirtyCheck();

    void addEntityEntry(Class<?> clazz, Long id, EntityEntry entityEntry);

    EntityEntry getEntityEntry(Class<?> clazz, Long id);


}
