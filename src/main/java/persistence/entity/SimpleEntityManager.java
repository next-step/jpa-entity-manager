package persistence.entity;

import domain.EntityMetaData;
import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.SelectQueryBuilder;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;
    private final JdbcTemplate jdbcTemplate;

    public SimpleEntityManager(EntityPersister entityPersister, JdbcTemplate jdbcTemplate) {
        this.entityPersister = entityPersister;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(new EntityMetaData(clazz));
        return jdbcTemplate.queryForObject(selectQueryBuilder.findByIdQuery(id), new RowMapperImpl<>(clazz));

    }

    public Object persist(Object object) {
        return entityPersister.insert(object);
    }

    @Override
    public boolean update(Object object) {
        return entityPersister.update(object);
    }

    @Override
    public void remove(Object object) {
        entityPersister.delete(object);
    }
}
