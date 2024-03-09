package persistence.sql.entity.impl;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.entity.EntityPersister;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.Table;

public class EntityPersisterImpl implements EntityPersister {

    private final UpdateQueryBuilder updateQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;
    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaCreator entityMetaCreator;

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate, EntityMetaCreator entityMetaCreator) {
        this.updateQueryBuilder = new UpdateQueryBuilder();
        this.insertQueryBuilder = new InsertQueryBuilder();
        this.deleteQueryBuilder = new DeleteQueryBuilder();
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetaCreator = entityMetaCreator;
    }

    @Override
    public boolean update(final Object object) {
        final Table table = entityMetaCreator.createByInstance(object);

        final int result = jdbcTemplate.executeUpdate(updateQueryBuilder.createUpdateQuery(table));
        return result > 0;
    }

    @Override
    public Long insert(final Object object) {
        final Table table = entityMetaCreator.createByInstance(object);

        return jdbcTemplate.executeAndReturnKey(insertQueryBuilder.createInsertQuery(table));
    }

    @Override
    public void delete(final Object object) {
        final Table table = entityMetaCreator.createByInstance(object);
        jdbcTemplate.execute(deleteQueryBuilder.createDeleteQuery(table));
    }
}
