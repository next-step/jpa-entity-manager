package persistence.entity.persistencecontext;

import java.util.Optional;

public interface PersistenceContext {

    <T> Optional<T> getEntity(Class<T> clazz, Long id);

    // TODO: (질문)파라메터에서 id가 있을 필요 없다고 생각하여 지웠는데, 혹시 id가 있어야 할 이유가 있을까요?
    void addEntity(Object entity);

    void removeEntity(Object entity);

    /**
     * 스냅샷을 이용해 데이터를 조회한다.
     */
    Object getDatabaseSnapshot(Object entity, Long id);
}
