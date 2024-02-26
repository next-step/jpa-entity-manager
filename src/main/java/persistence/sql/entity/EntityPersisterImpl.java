package persistence.sql.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class EntityPersisterImpl implements EntityPersister {

    private final UpdateQueryBuilder updateQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersisterImpl(UpdateQueryBuilder updateQueryBuilder, InsertQueryBuilder insertQueryBuilder,
                               DeleteQueryBuilder deleteQueryBuilder, JdbcTemplate jdbcTemplate) {
        this.updateQueryBuilder = updateQueryBuilder;
        this.insertQueryBuilder = insertQueryBuilder;
        this.deleteQueryBuilder = deleteQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean update(final Object object) {
        final int result = jdbcTemplate.executeUpdate(updateQueryBuilder.createUpdateQuery(object));
        return result > 0;
    }

    @Override
    public void insert(final Object object) {
        jdbcTemplate.execute(insertQueryBuilder.createInsertQuery(object));
    }

    @Override
    public void delete(final Object object) {
        jdbcTemplate.execute(deleteQueryBuilder.createDeleteQuery(object));
    }
}
