package persistence.entity;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class DefaultEntityManager<T> implements EntityManager<T> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEntityManager.class);

    private final JdbcTemplate jdbcTemplate;

    DefaultEntityManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public T find(Class<T> entityType, Object id) {
        final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(entityType);
        final String sql = selectQueryBuilder.findById(id);
        return jdbcTemplate.queryForObject(sql, new DefaultRowMapper<>(entityType));
    }

    @Override
    public void persist(Object entity) {
        final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(entity);
        final String sql = insertQueryBuilder.insert();
        jdbcTemplate.execute(sql);
    }

    @Override
    public void remove(Object entity) {
        final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(entity);
        jdbcTemplate.execute(deleteQueryBuilder.delete());
    }

    @Override
    public void update(Object entity) {
        final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(entity);
        jdbcTemplate.execute(updateQueryBuilder.update());
    }

}
