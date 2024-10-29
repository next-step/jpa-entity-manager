package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.DmlQueryBuilder;

public class EntityLoaderImpl implements EntityLoader {
    JdbcTemplate jdbcTemplate;
    DmlQueryBuilder dmlQueryBuilder;

    public EntityLoaderImpl(JdbcTemplate jdbcTemplate, DmlQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        String selectQuery = dmlQueryBuilder.buildSelectByIdQuery(clazz, id);
        return jdbcTemplate.queryForObject(
                selectQuery,
                resultSet -> new RowMapperImpl<>(clazz).mapRow(resultSet)
        );
    }
}
