package persistence.entity.persistencecontext;

import java.util.Optional;

public interface PersistenceContext {

    <T> Optional<T> getEntity(Class<T> clazz, Long id);

    <T> T addEntity(T entity, Long id);

    <T> T updateEntity(T entity, Long id);

    <T> void removeEntity(T entity);

    /**
     * 스냅샷을 이용해 데이터를 조회한다.
     */
    <T> T getDatabaseSnapshot(T entity, Long id);

    <T> boolean isDirty(T entity);

    Optional<EntityEntry> getEntityEntry(Class<?> clazz, Long id);
    <T> EntityEntry getEntityEntry(T entity);
}
