package jpa;

import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQuery;
import persistence.sql.entity.EntityRowMapper;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T find(Class<T> clazz, Object id) {
        SelectQuery selectQuery = SelectQuery.getInstance();
        return jdbcTemplate.queryForObject(selectQuery.findById(clazz, id), new EntityRowMapper<>(clazz));
    }
}
