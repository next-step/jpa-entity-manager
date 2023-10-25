package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.Query;

public class EntityPersister {

    private final Query query;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(Query query, JdbcTemplate jdbcTemplate) {
        this.query = query;
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> void update(T entity) {
        jdbcTemplate.execute(query.update(entity));
    }

    public <T> void insert(T entity) {
        jdbcTemplate.execute(query.insert(entity));
    }

    public <T> void delete(T entity) {
        jdbcTemplate.execute(query.delete(entity));
    }

}
