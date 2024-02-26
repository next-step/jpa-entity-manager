package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.sql.dml.SelectQueryBuilder;

public class MyEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;

    public MyEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = SelectQueryBuilder.getInstance();
    }

    @Override
    public <T> T find(Class<T> clazz, Object Id) {
        String query = selectQueryBuilder.build(clazz, Id);
        RowMapper<T> rowMapper = RowMapperFactory.create(clazz);
        return jdbcTemplate.queryForObject(query, rowMapper);
    }
}
