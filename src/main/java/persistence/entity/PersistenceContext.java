package persistence.entity;

import java.util.Optional;

public interface PersistenceContext {

    Optional<Object> getEntity(Long id);

    void addEntity(Long id, Object entity);

    void removeEntity(Long id);

    /*
    스냅샷을 만들 때 Object 가 아니라 EntityPersister 라는 인터페이스를 활용해 엔티티가 영속화 될 때 아래의 메서드를 호출
    너무 많은 로직이 있기에 간단하게 구현
     */
    Object getDatabaseSnapshot(Long id, Object entity);

     <T> T getCachedDatabaseSnapshot(Long id);
}
