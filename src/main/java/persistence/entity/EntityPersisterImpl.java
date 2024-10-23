package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;

public class EntityPersisterImpl implements EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    private final DmlQueryBuilder dmlQueryBuilder;

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate, DmlQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    @Override
    public void update(Object entity) {
        jdbcTemplate.execute(dmlQueryBuilder.buildUpdateQuery(entity));
    }

    @Override
    public void insert(Object entity) {
        jdbcTemplate.execute(dmlQueryBuilder.buildInsertQuery(entity));
    }

    @Override
    public void delete(Object entity) {
        jdbcTemplate.execute(dmlQueryBuilder.buildDeleteQuery(entity));
    }
}
