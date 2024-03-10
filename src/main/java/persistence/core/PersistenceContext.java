package persistence.core;

import java.util.List;

public interface PersistenceContext {
    Object getEntity(Class<?> entity, Long id);

    void addEntity(Long id, Object entity);

    void removeEntity(Object entity);

    void getDatabaseSnapshot(Long id, Object entity);

    <T> List<T> dirtyCheck();


}
