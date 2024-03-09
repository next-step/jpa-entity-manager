package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class EntityPersisterImpl implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean update(Object entity) {
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(entity);
        return jdbcTemplate.execute(updateQueryBuilder.build());
    }

    @Override
    public Object insert(Object entity) {
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(entity);
        return jdbcTemplate.executeInsertQuery(insertQueryBuilder.build());
    }

    @Override
    public void delete(Object entity) {
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(entity);
        jdbcTemplate.execute(deleteQueryBuilder.build());
    }
}
