package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.QueryDml;

import java.sql.Connection;

public class EntityPersister {
    private JdbcTemplate jdbcTemplate;

    public EntityPersister(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
    }

    public <T> boolean update(T t, Object arg) {
        try {
            jdbcTemplate.execute(QueryDml.update(t, arg));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <T> void insert(T t) {
        jdbcTemplate.execute(QueryDml.insert(t));
    }

    public <T> void delete(T t) {
        jdbcTemplate.execute(QueryDml.delete(t));
    }
}
