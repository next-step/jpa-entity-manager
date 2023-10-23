package persistence.entity;

import jdbc.JdbcTemplate;

import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;


public class EntityPersister<T> {
    private final JdbcTemplate jdbcTemplate;
    private final EntityMeta entityMeta;

    public EntityPersister(JdbcTemplate jdbcTemplate, Class<T> tClass) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMeta = new EntityMeta(tClass);
    }

    public void insert(T entity) {
        final String query = QueryGenerator.from(entityMeta).insert(entity);
        jdbcTemplate.execute(query);
    }

    public boolean update(T entity) {
        final String query = QueryGenerator.from(entityMeta).update(entity);
        jdbcTemplate.execute(query);
        return true;
    }

}
