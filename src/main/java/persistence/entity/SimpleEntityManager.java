package persistence.entity;

import dialect.Dialect;
import persistence.context.PersistenceContext;

import java.util.Objects;

public class SimpleEntityManager implements EntityManager {

    /**
     * 1. 영속 컨텍스트 내에서 Entity 를 조회
     * 2. 조회된 상태의 Entity 를 스냅샷 생성
     * 3. 트랜잭션 커밋 후 해당 스냅샷과 현재 Entity 를 비교 (데이터베이스 커밋은 신경쓰지 않는다)
     * 4. 다른 점을 쿼리로 생성
     */
    private final Dialect dialect;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public SimpleEntityManager(Dialect dialect, EntityPersister entityPersister, EntityLoader entityLoader,
                               PersistenceContext persistenceContext) {
        this.dialect = dialect;
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Object entity, Class<T> clazz, Long id) {
        T cachedEntity = persistenceContext.getEntity(clazz, id);
        if (Objects.nonNull(cachedEntity)) {
            return cachedEntity;
        }
        T loadedEntity = entityLoader.findById(clazz, entity, id);
        persistenceContext.addEntity(id, loadedEntity);
        return loadedEntity;
    }

    @Override
    public <T> T persist(T entity) {
        entityPersister.insert(entity);
        persistenceContext.addEntity(entity);
        return entity;
    }

    @Override
    public boolean update(Object entity) {
        return entityPersister.update(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }

    /**
     * 변경이 감지된 엔티티 객체는 영속성 컨텍스트에 해당 변경 내용을 반영
     * 변경된 엔티티 객체를 영속성 컨텍스트에 병합하여 영속성 컨텍스트의 상태를 업데이트하는 역할을 수행
     */
    @Override
    public <T> T merge(T entity) {
        EntitySnapshot cachedDatabaseSnapshot = persistenceContext.getDatabaseSnapshot(entity);
        EntitySnapshot entitySnapshot = new EntitySnapshot(entity);

        if (!Objects.equals(cachedDatabaseSnapshot, entitySnapshot)) {
            update(entity);
            persistenceContext.addEntity(entity);
        }
        return entity;
    }
}
