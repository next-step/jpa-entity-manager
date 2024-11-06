package persistence.entity.impl;

import jdbc.JdbcTemplate;
import persistence.defaulthibernate.EntryStatus;
import persistence.entity.EntityData;
import persistence.entity.EntityKey;
import persistence.entity.EntityManager;
import persistence.defaulthibernate.DefaultPersistenceContext;
import persistence.sql.TableId;
import persistence.sql.TableMeta;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
        // 스냅샷 저장
        EntityKey entityKey = new EntityKey(id, clazz);
        if (defaultPersistenceContext.isExist(entityKey)) {
            Object o = defaultPersistenceContext.get(entityKey);
            return Optional.of(clazz.cast(o));
        }
        defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.LOADING);
        Optional<T> t = entityPersister.find(clazz, id);
        defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.MANAGED);

        if (t.isEmpty()) {
            return Optional.empty();  // 엔티티가 없는 경우 빈 Optional 반환
        }

        // 엔티티가 타입에 맞는지 확인하고 캐시
        T entity = clazz.cast(t.get());
        defaultPersistenceContext.add(entity, id);

        return Optional.of(entity);  // 조회된 엔티티 반환
    }

    @Override
    public Object persist(Object entity) throws NoSuchFieldException, IllegalAccessException {
        // 스냅샷 저장
        EntityData entityData = new EntityData(entity);
        EntityKey entityKey = new EntityKey(entityData.getId(), entity.getClass());

        defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.SAVING);
        Long id = entityPersister.insert(entity);

        entityKey = new EntityKey(id, entity.getClass());
        defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.MANAGED);
        defaultPersistenceContext.add(entityData, entityKey);

        return entity;
    }

    @Override
    public void remove(Class<?> clazz, Long id) {
        EntityKey entityKey = new EntityKey(id, clazz);
        defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.DELETED);
        entityPersister.remove(clazz, id);
        defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.GONE);
        if (defaultPersistenceContext.isExist(entityKey)) {
            defaultPersistenceContext.remove(entityKey);
        }
    }

    @Override
    public void update(Object entity) {
        Class<?> clazz = entity.getClass();
        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            Long id = (Long) idField.get(entity);
            EntityKey entityKey = new EntityKey(id, clazz);
            EntityData entityData = new EntityData(entity);
            defaultPersistenceContext.update(entityData, entityKey);
            defaultPersistenceContext.setEntityEntryStatus(entityKey, EntryStatus.MANAGED);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to update entity", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flush() {
        defaultPersistenceContext.getDirtyObjects().forEach(entityPersister::update);
        defaultPersistenceContext.getDirtyObjects().forEach(o->{
            try {
                Class<?> clazz = o.getClass();
                Field idField = clazz.getDeclaredField("id");
                idField.setAccessible(true);
                Long id = (Long) idField.get(o);
                EntityKey entityKey = new EntityKey(id, clazz);
                defaultPersistenceContext.setEntityEntryStatus( entityKey, EntryStatus.GONE);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        defaultPersistenceContext.clearSnapshots();
    }
}
