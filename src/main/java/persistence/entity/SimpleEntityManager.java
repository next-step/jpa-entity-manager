package persistence.entity;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;

    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;


    private SimpleEntityManager(JdbcTemplate jdbcTemplate) {
        entityPersister = EntityPersister.from(jdbcTemplate);
        dmlGenerator = DmlGenerator.getInstance();
        this.jdbcTemplate = jdbcTemplate;
    }

    public static SimpleEntityManager from(JdbcTemplate jdbcTemplate) {
        return new SimpleEntityManager(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return jdbcTemplate.queryForObject(dmlGenerator.generateSelectQuery(clazz, id),
            resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));
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
