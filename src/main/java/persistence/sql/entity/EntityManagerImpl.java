package persistence.sql.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;

import static persistence.sql.entity.RowMapperFactory.createRowMapper;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final JdbcTemplate jdbcTemplate;

    public EntityManagerImpl(final EntityPersister entityPersister, final JdbcTemplate jdbcTemplate) {
        this.entityPersister = entityPersister;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Long Id) {
        final SelectQueryBuilder queryBuilder = new SelectQueryBuilder(SimpleEntityMetaCreator.of(clazz));
        final String findByIdQuery = queryBuilder.createFindByIdQuery(Id);

        return jdbcTemplate.queryForObject(findByIdQuery, createRowMapper(clazz));
    }

    @Override
    public void persist(final Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void remove(final Object entity) {
        entityPersister.delete(entity);
    }
}
