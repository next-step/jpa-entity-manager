package database;

import jdbc.JdbcTemplate;
import persistence.entity.EntityId;

import java.util.List;

public class SimpleDatabase implements Database {

    private final JdbcTemplate jdbcTemplate;

    public SimpleDatabase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void execute(String sql) {
        jdbcTemplate.execute(sql);
    }

    @Override
    public EntityId executeQueryAndGetGeneratedKey(String sql) {
        Object idValue = jdbcTemplate.queryAndGetGeneratedKey(sql);
        return new EntityId(idValue);
    }

    @Override
    public <T> T executeQueryForObject(Class<T> clazz, String sql) {
        return jdbcTemplate.queryForObject(sql, new EntityRowMapper<>(clazz));
    }

    @Override
    public <T> List<T> executeQuery(Class<T> clazz, String sql) {
        return jdbcTemplate.query(sql, new EntityRowMapper<>(clazz));
    }
}
