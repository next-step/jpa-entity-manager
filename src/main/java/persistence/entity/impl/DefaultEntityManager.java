package persistence.entity.impl;

import jdbc.JdbcTemplate;
import persistence.entity.EntityManager;
import persistence.entity.EntityRowMapper;
import persistence.fakehibernate.FakePersistenceContext;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultEntityManager implements EntityManager {

//    private final Map<Class<?>, Map<Long, Object>> entityCache;

    private FakePersistenceContext fakePersistenceContext;
    private EntityPersister entityPersister;

    public DefaultEntityManager(JdbcTemplate jdbcTemplate) {
        this.fakePersistenceContext = new FakePersistenceContext();
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public <T> Optional<T> find(Class<T> clazz, Long id) {

        if (fakePersistenceContext.isExist(clazz, id)) {
            return Optional.of(clazz.cast(fakePersistenceContext.get(clazz, id)));  // 캐시된 엔티티 반환
        }

        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz);
        String selectQuery = selectQueryBuilder.findById(clazz, id);
        List<T> query = jdbcTemplate.query(selectQuery, new EntityRowMapper<>(clazz));

        if (query.isEmpty()) {
            return Optional.empty();  // 엔티티가 없는 경우 빈 Optional 반환
        }

        // 엔티티가 타입에 맞는지 확인하고 캐시
        T entity = query.getFirst();
        fakePersistenceContext.add(entity, id);
        return Optional.of(entity);  // 조회된 엔티티 반환
    }

    @Override
    public Object persist(Object entity) {
        Class<?> clazz = entity.getClass();
        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(clazz);
            String insertQuery = insertQueryBuilder.insert(entity);
            Long id = jdbcTemplate.executeInsert(insertQuery);
            fakePersistenceContext.add(entity, id);
            return entity;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to persist entity", e);
        }
    }

    @Override
    public void remove(Class<?> clazz, Long id) {
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(clazz);
        String deleteQuery = deleteQueryBuilder.deleteById(clazz, id);
        jdbcTemplate.execute(deleteQuery);

        if (fakePersistenceContext.isExist(clazz, id)) {
            fakePersistenceContext.remove(clazz, id);
        }
    }

    @Override
    public void update(Object entity) {
        Class<?> clazz = entity.getClass();
        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            Long id = (Long) idField.get(entity);

            UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(clazz);
            String updateQuery = updateQueryBuilder.update(entity);
            jdbcTemplate.execute(updateQuery);
            fakePersistenceContext.update(entity, id);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to update entity", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}