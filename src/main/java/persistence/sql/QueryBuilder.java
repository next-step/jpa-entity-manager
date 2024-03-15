package persistence.sql;


import java.lang.reflect.Field;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class QueryBuilder extends AbstractQueryBuilder {

    private final SelectQueryBuilder selectQueryBuilder;

    private final DeleteQueryBuilder deleteQueryBuilder;

    private final InsertQueryBuilder insertQueryTranslator;

    private final DropQueryBuilder dropQueryBuilder;

    private final CreateQueryBuilder createQueryBuilder;

    private final UpdateQueryBuilder updateQueryBuilder;

    public QueryBuilder() {
        this.selectQueryBuilder = new SelectQueryBuilder();
        this.deleteQueryBuilder = new DeleteQueryBuilder();
        this.insertQueryTranslator = new InsertQueryBuilder();
        this.dropQueryBuilder = new DropQueryBuilder();
        this.createQueryBuilder = new CreateQueryBuilder();
        this.updateQueryBuilder = new UpdateQueryBuilder();
    }

    public String getCreateTableQuery(final Class<?> entityClass) {
        return createQueryBuilder.getCreateTableQuery(entityClass);
    }

    public String getDropTableQuery(Class<?> entityClass) {
        return dropQueryBuilder.getDropTableQuery(entityClass);
    }

    public String getInsertQuery(Object entity) {
        return insertQueryTranslator.getInsertQuery(entity);
    }

    public String getSelectAllQuery(Class<?> entityClass) {
        return selectQueryBuilder.getSelectAllQuery(entityClass);
    }

    public String getSelectByIdQuery(Class<?> entityClass, Object id) {
        return selectQueryBuilder.getSelectByIdQuery(entityClass, id);
    }

    public String getSelectCountQuery(Class<?> entityClass) {
        return selectQueryBuilder.getSelectCountQuery(entityClass);
    }

    public String getUpdateQuery(Object entity) {
        return updateQueryBuilder.getUpdateQuery(entity);
    }

    public String getDeleteAllQuery(Class<?> entityClass) {
        return deleteQueryBuilder.getDeleteAllQuery(entityClass);
    }

    public String getDeleteByIdQuery(Class<?> entityClass, Object id) {
        return deleteQueryBuilder.getDeleteByIdQuery(entityClass, id);
    }

    public String getDeleteQueryFromEntity(Object entity) {
        return deleteQueryBuilder.getDeleteQueryFromEntity(entity);
    }

    // TODO: Remove this method because it is used only in tests
    public String getTableNameFrom(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        return entityMetadata.getTableName();
    }

    // TODO: Remove this method because it is used only in tests
    public String getColumnDefinitionFrom(Field field) {
        return createQueryBuilder.getColumnDefinitionFrom(field);
    }

    // TODO: Remove this method because it is used only in tests
    public String getColumnDefinitionsFrom(Class<?> entityClass) {
        return createQueryBuilder.getColumnDefinitionsFrom(entityClass);
    }
}
