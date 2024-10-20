package persistence.entity;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class DefaultEntityPersister implements EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    public DefaultEntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> entityType, Object id) {
        final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(entityType);
        final String sql = selectQueryBuilder.findById(id);
        return jdbcTemplate.queryForObject(sql, new DefaultRowMapper<>(entityType));
    }

    @Override
    public void insert(Object entity) {
        final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(entity);
        final String sql = insertQueryBuilder.insert();
        jdbcTemplate.execute(sql);
    }

    @Override
    public void update(Object entity) {
        final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(entity);
        jdbcTemplate.execute(updateQueryBuilder.update());
    }

    @Override
    public void delete(Object entity) {
        final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(entity);
        jdbcTemplate.execute(deleteQueryBuilder.delete());
    }
}
