package persistence.entity;

import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.DeleteByIdQueryBuilder;
import persistence.sql.dml.query.InsertQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

public class EntityPersister {

    private static final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private static final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private static final DeleteByIdQueryBuilder deleteByIdQueryBuilder = new DeleteByIdQueryBuilder();

    private final TableDefinition tableDefinition;


    public EntityPersister(Class<?> clazz) {
        this.tableDefinition = new TableDefinition(clazz);
    }

    public Object getEntityId(Object entity) {
        return tableDefinition.tableId().getValue(entity);
    }

    public String buildUpdateQuery(Object entity) {
        return updateQueryBuilder.build(entity);
    }

    public String buildInsertQuery(Object entity) {
        return insertQueryBuilder.build(entity);
    }

    public String buildDeleteQuery(Object entity) {
        return deleteByIdQueryBuilder.build(entity);
    }
}
