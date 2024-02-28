package persistence.sql.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.meta.EntityMetaCreator;

import static persistence.sql.entity.RowMapperFactory.createRowMapper;

public class EntityLoaderImpl implements EntityLoader {

    private final EntityMetaCreator entityMetaCreator;
    private final JdbcTemplate jdbcTemplate;


    public EntityLoaderImpl(EntityMetaCreator entityMetaCreator, JdbcTemplate jdbcTemplate) {
        this.entityMetaCreator = entityMetaCreator;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T findById(final Class<T> clazz, final Long id) {
        final SelectQueryBuilder queryBuilder = new SelectQueryBuilder(entityMetaCreator);
        final String findByIdQuery = queryBuilder.createFindByIdQuery(id);

        return jdbcTemplate.queryForObject(findByIdQuery, createRowMapper(clazz));
    }
}
