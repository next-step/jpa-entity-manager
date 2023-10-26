package persistence.entity;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.DialectFactory;
import persistence.sql.dialect.h2.H2Dialect;
import persistence.sql.dml.DmlQueryGenerator;

public class DefaultEntityManager implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;

    private DefaultEntityManager(JdbcTemplate jdbcTemplate, EntityPersister entityPersister) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersister = entityPersister;
    }

    public static DefaultEntityManager of(JdbcTemplate jdbcTemplate) {
        return new DefaultEntityManager(jdbcTemplate, EntityPersister.of(jdbcTemplate));
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        // TODO : step 2-2 에서 EntityLoader 를 통해 구현
        DmlQueryGenerator dmlQueryGenerator = DmlQueryGenerator.of(H2Dialect.getInstance());
        String selectByPkQuery = dmlQueryGenerator.generateSelectByPkQuery(clazz, id);
        return jdbcTemplate.queryForObject(selectByPkQuery, new EntityRowMapper<>(clazz));
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
