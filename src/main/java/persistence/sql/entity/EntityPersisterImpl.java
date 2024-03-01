package persistence.sql.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.meta.Columns;
import persistence.sql.meta.PrimaryKey;

public class EntityPersisterImpl implements EntityPersister {

    private final String tableName;
    private final PrimaryKey primaryKey;
    private final Columns columns;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersisterImpl(String tableName, PrimaryKey primaryKey, Columns columns, JdbcTemplate jdbcTemplate) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.columns = columns;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean update(final Object object) {
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(tableName, primaryKey, columns);
        final int result = jdbcTemplate.executeUpdate(updateQueryBuilder.createUpdateQuery(object));
        return result > 0;
    }

    @Override
    public void insert(final Object object) {
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(tableName, columns);
        jdbcTemplate.execute(insertQueryBuilder.createInsertQuery(object));
    }

    @Override
    public void delete(final Object object) {
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(tableName, primaryKey);
        jdbcTemplate.execute(deleteQueryBuilder.createDeleteQuery(object));
    }
}
