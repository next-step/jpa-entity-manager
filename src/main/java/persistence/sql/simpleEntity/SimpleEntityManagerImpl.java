package persistence.sql.simpleEntity;

import java.util.List;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.FindByIdQuery;
import jdbc.EntityLoader;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.UpdateQuery;

public class SimpleEntityManagerImpl<T, ID> implements SimpleEntityManager<T, ID> {

    private final Class<T> entityClass;
    private final FindByIdQuery findByIdQuery;
    private final InsertQuery insertQuery;
    private final DeleteQuery deleteQuery;
    private final UpdateQuery updateQuery;
    private final JdbcTemplate jdbcTemplate;

    public SimpleEntityManagerImpl(Class<T> entityClass, JdbcTemplate jdbcTemplate) {
        this.entityClass = entityClass;
        this.findByIdQuery = new FindByIdQuery(entityClass);
        this.insertQuery = new InsertQuery();
        this.deleteQuery = new DeleteQuery();
        this.updateQuery = new UpdateQuery();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public T findById(ID id) {
        String sql = findByIdQuery.generateQuery(id);
        return jdbcTemplate.queryForObject(sql, new EntityLoader<>(entityClass));
    }

    @Override
    public void persist(T entity) throws IllegalAccessException {
        String sql = insertQuery.generateQuery(entity);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void remove(T entity) {
        String sql = deleteQuery.generateQuery(entity);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void update(T entity, List<String> changedColumns) throws IllegalAccessException {
        String sql = updateQuery.generateQuery(entity, changedColumns);
        jdbcTemplate.execute(sql);
    }

}
