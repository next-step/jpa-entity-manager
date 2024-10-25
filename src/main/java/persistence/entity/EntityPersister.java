package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.FindByIdQuery;
import jdbc.EntityLoader;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.UpdateQuery;

public class EntityPersister<T> {

    private final JdbcTemplate jdbcTemplate;
    private final FindByIdQuery findByIdQuery;
    private final EntityLoader<T> entityLoader;

    public EntityPersister(Class<T> entityClass, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.findByIdQuery = new FindByIdQuery(entityClass);
        this.entityLoader = new EntityLoader<>(entityClass);
    }

    public T findById(Object primaryKey) {
        String sql = findByIdQuery.generateFindByIdQuery(primaryKey);
        return jdbcTemplate.queryForObject(sql, entityLoader);
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
