package persistence.entity.impl;

import jdbc.JdbcTemplate;
import persistence.defaulthibernate.EntryStatus;
import persistence.entity.EntityData;
import persistence.entity.EntityKey;
import persistence.entity.EntityManager;
import persistence.defaulthibernate.DefaultPersistenceContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;


public class DefaultEntityManager implements EntityManager {
    private final DefaultPersistenceContext defaultPersistenceContext;
    private final EntityPersister entityPersister;

    public DefaultEntityManager(JdbcTemplate jdbcTemplate) {
        this.defaultPersistenceContext = new DefaultPersistenceContext();
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public <T> Optional<T> find(Class<T> clazz, Long id) {
        EntityKey entityKey = new EntityKey(id, clazz);

        // 1차 캐시 확인
        if (defaultPersistenceContext.isExist(entityKey)) {
            EntityData entityData = defaultPersistenceContext.get(entityKey);
            return Optional.of(clazz.cast(entityData.entity()));
        }

        // DB 조회
        defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.LOADING);
        Optional<T> entityOptional = entityPersister.find(clazz, id);

        if (entityOptional.isEmpty()) {
            return Optional.empty();
        }

        // 엔티티 캐시에 저장
        T entity = entityOptional.get();
        EntityData entityData = new EntityData(entity);
        defaultPersistenceContext.add(entityData, entityKey);
        defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.MANAGED);

        return Optional.of(entity);
    }

    @Override
    public Object persist(Object entity) throws NoSuchFieldException, IllegalAccessException {
        EntityData entityData = new EntityData(entity);
        EntityKey entityKey = new EntityKey(entityData.getId(), entity.getClass());

        // 저장 전 상태 설정
        defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.SAVING);

        // DB 저장
        Long id = entityPersister.insert(entity);

        // 새로운 ID로 엔티티 키 생성
        EntityKey newEntityKey = new EntityKey(id, entity.getClass());
        EntityData newEntityData = new EntityData(id, entity.getClass(), entity);

        // 영속성 컨텍스트에 저장
        defaultPersistenceContext.add(newEntityData, newEntityKey);
        defaultPersistenceContext.setEntityEntryStatus(newEntityKey, EntryStatus.MANAGED);

        return entity;
    }

    @Override
    public void remove(Class<?> clazz, Long id) {
        EntityKey entityKey = new EntityKey(id, clazz);

        // 삭제 처리
        defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.DELETED);
        entityPersister.remove(clazz, id);

        // 영속성 컨텍스트에서 제거
        defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.GONE);
        if (defaultPersistenceContext.isExist(entityKey)) {
            defaultPersistenceContext.remove(entityKey);
        }
    }

    @Override
    public void update(Object entity) {
        try {
            Class<?> clazz = entity.getClass();
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            Long id = (Long) idField.get(entity);

            EntityKey entityKey = new EntityKey(id, clazz);
            EntityData entityData = new EntityData(entity);

            // 영속성 컨텍스트 업데이트
            defaultPersistenceContext.update(entityData, entityKey);
            defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.MANAGED);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to update entity", e);
        }
    }

    @Override
    public void flush() {
        // 변경된 엔티티들 DB 업데이트
        List<Object> dirtyObjects = defaultPersistenceContext.getDirtyObjects();

        for (Object dirtyObject : dirtyObjects) {
            try {
                // DB 업데이트
                entityPersister.update(dirtyObject);

                // 상태 업데이트
                Class<?> clazz = dirtyObject.getClass();
                Field idField = clazz.getDeclaredField("id");
                idField.setAccessible(true);
                Long id = (Long) idField.get(dirtyObject);
                EntityKey entityKey = new EntityKey(id, clazz);

                defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.MANAGED);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to flush entity", e);
            }
        }

        // 스냅샷 초기화
        defaultPersistenceContext.clearSnapshots();
    }
}
