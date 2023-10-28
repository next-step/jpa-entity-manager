package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.Query;

public class EntityLoader {

    private final Query query;
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(Query query, JdbcTemplate jdbcTemplate) {
        this.query = query;
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T, K> T selectById(Class<T> entityClass, K primaryKey) {
        return jdbcTemplate.queryForObject(query.findById(entityClass, primaryKey), new SimpleRowMapper<>(entityClass));
    }

}
