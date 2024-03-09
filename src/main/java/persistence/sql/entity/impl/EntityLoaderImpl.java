package persistence.sql.entity.impl;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.entity.EntityLoader;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.Table;
import persistence.sql.meta.simple.SimpleRowMapper;

public class EntityLoaderImpl implements EntityLoader {

    private final SelectQueryBuilder selectQueryBuilder;
    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaCreator entityMetaCreator;

    public EntityLoaderImpl(JdbcTemplate jdbcTemplate, EntityMetaCreator entityMetaCreator) {
        this.selectQueryBuilder = new SelectQueryBuilder();
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetaCreator = entityMetaCreator;
    }

    @Override
    public <T> T findById(final Class<T> clazz, final Long id) {
        final Table table = entityMetaCreator.createByClass(clazz);
        final String findByIdQuery = selectQueryBuilder.createFindByIdQuery(table, id);
        final RowMapper<T> simpleRowMapper = new SimpleRowMapper<>(clazz);
        return jdbcTemplate.queryForObject(findByIdQuery, simpleRowMapper);
    }
}
