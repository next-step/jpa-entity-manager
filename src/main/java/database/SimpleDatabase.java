package database;

import jdbc.JdbcTemplate;

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
    public <T> T executeQueryForObject(Class<T> clazz, String sql) {
        return jdbcTemplate.queryForObject(sql, new EntityRowMapper<>(clazz));
    }
}
