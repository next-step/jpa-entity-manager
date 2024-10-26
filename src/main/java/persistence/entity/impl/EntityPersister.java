package persistence.entity.impl;

import jdbc.JdbcTemplate;
import persistence.sql.TableMeta;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private final TableMeta tableMeta;

    public EntityPersister(JdbcTemplate jdbcTemplate, TableMeta tableMeta) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableMeta = tableMeta;
    }

    public boolean update(Object entity) {
        return false;
    }

    public boolean remove(Class<?> clazz, Long id) {
        return false;
    }
    public Long persist(Object entity) {
        return null;
    }
}
