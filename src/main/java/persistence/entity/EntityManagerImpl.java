package persistence.entity;

import jdbc.GenericRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;

public class EntityManagerImpl implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersister = new EntityPersisterImpl(jdbcTemplate);
        this.entityLoader = new EntityLoaderImpl(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz, Id);
        return jdbcTemplate.queryForObject(selectQueryBuilder.build(),
                resultSet -> new GenericRowMapper<T>(clazz).mapRow(resultSet));
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
