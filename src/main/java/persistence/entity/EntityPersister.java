package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.FindByIdQuery;
import persistence.sql.dml.GenericRowMapper;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.UpdateQuery;

public class EntityPersister<T> {

    private final Class<T> entityClass;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(Class<T> entityClass, JdbcTemplate jdbcTemplate) {
        this.entityClass = entityClass;
        this.jdbcTemplate = jdbcTemplate;
    }

    public T findById(Object primaryKey) {
        String sql = new FindByIdQuery<>(entityClass, primaryKey).generateQuery();
        return jdbcTemplate.queryForObject(sql, new GenericRowMapper<>(entityClass));
    }

    public void update(Object entity) throws IllegalAccessException {
        String sql = new UpdateQuery<>(entity).generateQuery();
        jdbcTemplate.execute(sql);
    }

    public void insert(Object entity) throws IllegalAccessException {
        String sql = new InsertQuery<>(entity).generateQuery();
        jdbcTemplate.execute(sql);
    }

    public void delete(Object entity) {
        String sql = new DeleteQuery<>(entity).generateQuery();
        jdbcTemplate.execute(sql);
    }

}
