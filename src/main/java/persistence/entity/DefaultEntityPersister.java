package persistence.entity;

import jdbc.DefaultIdMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.meta.EntityColumn;

import java.util.List;

public class DefaultEntityPersister implements EntityPersister {
    
    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public DefaultEntityPersister(JdbcTemplate jdbcTemplate, InsertQueryBuilder insertQueryBuilder,
                                  UpdateQueryBuilder updateQueryBuilder, DeleteQueryBuilder deleteQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertQueryBuilder = insertQueryBuilder;
        this.updateQueryBuilder = updateQueryBuilder;
        this.deleteQueryBuilder = deleteQueryBuilder;
    }

    @Override
    public void insert(Object entity) {
        final String sql = insertQueryBuilder.insert(entity);
        jdbcTemplate.executeAndReturnGeneratedKeys(sql, new DefaultIdMapper(entity));
    }

    @Override
    public void update(Object entity, List<EntityColumn> entityColumns) {
        jdbcTemplate.execute(updateQueryBuilder.update(entity, entityColumns));
    }

    @Override
    public void delete(Object entity) {
        jdbcTemplate.execute(deleteQueryBuilder.delete(entity));
    }
}
