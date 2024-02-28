package persistence.sql.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.meta.EntityMetaCreator;

public class EntityPersisterImpl implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaCreator entityMetaCreator;

    public EntityPersisterImpl(EntityMetaCreator entityMetaCreator, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetaCreator = entityMetaCreator;
    }

    @Override
    public boolean update(final Object object) {
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(entityMetaCreator);
        final int result = jdbcTemplate.executeUpdate(updateQueryBuilder.createUpdateQuery(object));
        return result > 0;
    }

    @Override
    public void insert(final Object object) {
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(entityMetaCreator);
        jdbcTemplate.execute(insertQueryBuilder.createInsertQuery(object));
    }

    @Override
    public void delete(final Object object) {
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(entityMetaCreator);
        jdbcTemplate.execute(deleteQueryBuilder.createDeleteQuery(object));
    }
}
