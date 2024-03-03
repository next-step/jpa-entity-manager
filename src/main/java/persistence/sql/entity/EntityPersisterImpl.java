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

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate) {
        this.updateQueryBuilder = new UpdateQueryBuilder();
        this.insertQueryBuilder = new InsertQueryBuilder();
        this.deleteQueryBuilder = new DeleteQueryBuilder();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean update(final Object object) {
        final int result = jdbcTemplate.executeUpdate(updateQueryBuilder.createUpdateQuery(object));
        return result > 0;
    }

    @Override
    public Long insert(final Object object) {
        return jdbcTemplate.executeAndReturnKey(insertQueryBuilder.createInsertQuery(object));
    }

    @Override
    public void delete(final Object object) {
        jdbcTemplate.execute(deleteQueryBuilder.createDeleteQuery(object));
    }
}
