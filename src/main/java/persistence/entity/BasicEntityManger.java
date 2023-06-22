package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class BasicEntityManger implements EntityManager {
    private final JdbcTemplate jdbcTemplate;

    public BasicEntityManger(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz);
        String selectQuery = selectQueryBuilder.findById(id);
        return jdbcTemplate.queryForObject(selectQuery, new RowMapperImpl<>(clazz));
    }
}
