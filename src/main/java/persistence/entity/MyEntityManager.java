package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQueryBuilder;

public class MyEntityManager implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final MyEntityPersister entityPersister;

    public MyEntityManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersister = new MyEntityPersister(jdbcTemplate);
    }

    //TODO entityLoader 생성 후 jdbcTemplate 제거 및 해당 메서드 책임 이전
    @Override
    public <T> T find(Class<T> clazz, Long Id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
        String query = selectQueryBuilder.build(clazz, Id);
        return jdbcTemplate.queryForObject(query, RowMapperFactory.create(clazz));
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
