package persistence.entity.impl;


import jdbc.JdbcTemplate;
import persistence.entity.EntityRowMapper;
import persistence.sql.dml.SelectQueryBuilder;

import java.util.List;


public class EntityLoader<T> {
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public T load(Class<T> clazz, Long id) {
        try {
            SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz);
            return jdbcTemplate.queryForObject(selectQueryBuilder.findById(clazz, id), new EntityRowMapper<>(clazz));
        } catch (RuntimeException e) {
            return null;
        }
    }

    public List<T> loadAll(Class<T> clazz) {
        try {
            SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz);
            return jdbcTemplate.query(selectQueryBuilder.findAll(clazz), new EntityRowMapper<>(clazz));
        } catch (RuntimeException e) {
            return null;
        }
    }
}
