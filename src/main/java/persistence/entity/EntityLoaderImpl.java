package persistence.entity;

import jdbc.GenericRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQueryBuilder;

public class EntityLoaderImpl implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;

    public EntityLoaderImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz, Id);
        return jdbcTemplate.queryForObject(selectQueryBuilder.build(),
                resultSet -> new GenericRowMapper<T>(clazz).mapRow(resultSet));
    }

}
