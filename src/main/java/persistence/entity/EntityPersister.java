package persistence.entity;

import jdbc.JdbcTemplate;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update() {
        return false;
    }

    public void insert() {
    }

    public void delete() {
    }
}
