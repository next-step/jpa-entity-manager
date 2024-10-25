package persistence.sql.dml;

import java.util.List;
import jdbc.EntityLoader;
import jdbc.JdbcTemplate;

public class JpaRepositoryImpl<T, ID> implements JpaRepository<T, ID> {

    private final Class<T> entityClass;
    private final InsertQuery insertQuery;
    private final FindByIdQuery findByIdQuery;
    private final FindAllQuery findAllQuery;
    private final DeleteByIdQuery deleteByIdQuery;
    private final DeleteQuery deleteQuery;
    private final DeleteAllQuery deleteAllQuery;
    private final JdbcTemplate jdbcTemplate;
    private final EntityLoader<T> entityLoader;

    public JpaRepositoryImpl(Class<T> entityClass, JdbcTemplate jdbcTemplate) {
        this.entityClass = entityClass;
        this.insertQuery = new InsertQuery();
        this.findByIdQuery = new FindByIdQuery(entityClass);
        this.findAllQuery = new FindAllQuery(entityClass);
        this.deleteByIdQuery = new DeleteByIdQuery(entityClass);
        this.deleteQuery = new DeleteQuery();
        this.deleteAllQuery = new DeleteAllQuery(entityClass);
        this.jdbcTemplate = jdbcTemplate;
        this.entityLoader = new EntityLoader<>(entityClass);
    }

    @Override
    public void save(T entity) throws IllegalAccessException {
        String sql = insertQuery.generateQuery(entity);
        jdbcTemplate.execute(sql);
    }

    @Override
    public T findById(ID id) {
        String sql = findByIdQuery.generateQuery(id);
        return jdbcTemplate.queryForObject(sql, entityLoader);
    }

    @Override
    public List<T> findAll() {
        String sql = findAllQuery.generateQuery();
        return jdbcTemplate.query(sql, new EntityLoader<>(entityClass));
    }

    @Override
    public void deleteById(ID id) {
        String sql = deleteByIdQuery.generateQuery(id);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void delete(T entity) {
        String sql = deleteQuery.generateQuery(entity);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void deleteAll() {
        String sql = deleteAllQuery.generateQuery();
        jdbcTemplate.execute(sql);
    }

}
