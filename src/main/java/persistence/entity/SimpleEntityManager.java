package persistence.entity;

import persistence.context.PersistenceContext;

import java.util.Objects;

public class SimpleEntityManager implements EntityManager {

    /**
     * 1. 영속 컨텍스트 내에서 Entity 를 조회
     * 2. 조회된 상태의 Entity 를 스냅샷 생성
     * 3. 트랜잭션 커밋 후 해당 스냅샷과 현재 Entity 를 비교 (데이터베이스 커밋은 신경쓰지 않는다)
     * 4. 다른 점을 쿼리로 생성
     */
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;
    private final EntityEntry entityEntry;

    public SimpleEntityManager(EntityPersister entityPersister, EntityLoader entityLoader,
                               PersistenceContext persistenceContext, EntityEntry entityEntry) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
        this.entityEntry = entityEntry;
    }

    @Override
    public <T> T find(Object entity, Class<T> clazz, Long id) {
        entityEntry.preFind();

        T cachedEntity = persistenceContext.getEntity(clazz, id);
        if (Objects.nonNull(cachedEntity)) {
            entityEntry.postFind();
            return cachedEntity;
        }
        T loadedEntity = entityLoader.findById(clazz, entity, id);
        persistenceContext.addEntity(id, loadedEntity);

        entityEntry.postFind();
        return loadedEntity;
    }

    @Override
    public <T> T persist(T entity) {
        entityEntry.prePersist();
        entityPersister.insert(entity);
        persistenceContext.addEntity(entity);
        entityEntry.postPersist();
        return entity;
    }

    @Override
    public boolean update(Object entity) {
        entityEntry.preUpdate();
        boolean update = entityPersister.update(entity);
        entityEntry.postUpdate();
        return update;
    }

    @Override
    public void remove(Object entity) {
        entityEntry.preRemove();
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
        entityEntry.postRemove();
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
            entityEntry.preUpdate();
            update(entity);
            persistenceContext.addEntity(entity);
            entityEntry.postUpdate();
        }
        return entity;
    }
}
