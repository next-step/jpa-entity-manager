package persistence.entity;

import jdbc.JdbcTemplate;

public interface EntityPersister {

    <T> void insert(T entity, JdbcTemplate jdbcTemplate);
    <T> void update(T entity, JdbcTemplate jdbcTemplate);
    <T> void delete(T entity, JdbcTemplate jdbcTemplate);

}
