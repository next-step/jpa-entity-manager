package persistence.entity.impl;

import jdbc.JdbcTemplate;
import persistence.entity.EntityManager;
import persistence.defaulthibernate.DefaultPersistenceContext;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * EntityPersister 주요 역활
 * JPA의 핵심 인터페이스로, 데이터베이스와 상호작용하면서 애플리케이션에서 엔티티 객체의 생명주기를 관리하는 역활
 * EntityMaanger는 개발자가 api를 통해서 엔티티 객체를 관리하는 인터페이스.
 * 엔티티의 생명주기 관리 (Persist, Merge, Remove )
 * 트랜잭션 관리
 * 구현체(hibernate, .. etc) 에 대한 인터페이스 제공
 */


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
        if (defaultPersistenceContext.isExist(clazz, id)) {
            Object o = defaultPersistenceContext.get(clazz, id);
            return Optional.of(clazz.cast(o));
        }
        Optional<T> t = entityPersister.find(clazz, id);

        if (t.isEmpty()) {
            return Optional.empty();  // 엔티티가 없는 경우 빈 Optional 반환
        }

        // 엔티티가 타입에 맞는지 확인하고 캐시
        T entity = clazz.cast(t.get());
        defaultPersistenceContext.add(entity, id);

        return Optional.of(entity);  // 조회된 엔티티 반환
    }

    @Override
    public Object persist(Object entity) {
        // 스냅샷 저장
        Long id = entityPersister.insert(entity);
        defaultPersistenceContext.add(entity, id);
        return entity;
    }

    @Override
    public void remove(Class<?> clazz, Long id) {
        entityPersister.remove(clazz, id);
        if (defaultPersistenceContext.isExist(clazz, id)) {
            defaultPersistenceContext.remove(clazz, id);
        }
    }

    @Override
    public void update(Object entity) {
        Class<?> clazz = entity.getClass();
        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            Long id = (Long) idField.get(entity);
            defaultPersistenceContext.update(entity, id);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to update entity", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flush() {
        defaultPersistenceContext.getDirtyObjects().forEach(entityPersister::update);
        defaultPersistenceContext.clearSnapshots();
    }
}
