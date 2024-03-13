package persistence.entity.persistencecontext;

import java.util.Optional;

public interface PersistenceContext {

    <T> Optional<T> getEntity(Class<T> clazz, Long id);

    Object addEntity(Object entity);

    Object updateEntity(Object entity, Long id);

    void removeEntity(Object entity);

    /**
     * 스냅샷을 이용해 데이터를 조회한다.
     */
    Optional<Object> getDatabaseSnapshot(Object entity, Long id);
}
