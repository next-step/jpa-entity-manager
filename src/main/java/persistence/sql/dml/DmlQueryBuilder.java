package persistence.sql.dml;

import persistence.sql.domain.Query;

public class DmlQueryBuilder implements InsertQueryBuild, SelectQueryBuild, DeleteQueryBuild, UpdateQueryBuild {

    private final InsertQueryBuild insertQueryBuilder;

    private final SelectQueryBuild selectQueryBuilder;

    private final DeleteQueryBuild deleteQueryBuilder;

    private final UpdateQueryBuild updateQueryBuilder;

    public DmlQueryBuilder() {
        this.insertQueryBuilder = new InsertQueryBuilder();
        this.selectQueryBuilder = new SelectQueryBuilder();
        this.deleteQueryBuilder = new DeleteQueryBuilder();
        this.updateQueryBuilder = new UpdateQueryBuilder();
    }


    @Override
    public <T> Query insert(T entity) {
        return insertQueryBuilder.insert(entity);
    }

    @Override
    public Query findAll(Class<?> entity) {
        return selectQueryBuilder.findAll(entity);
    }

    @Override
    public Query findById(Class<?> entity, Object id) {
        return selectQueryBuilder.findById(entity, id);

    }

    @Override
    public <T> Query delete(T entity) {
        return deleteQueryBuilder.delete(entity);
    }

    @Override
    public Query update(Object entity, Object id) {
        return updateQueryBuilder.update(entity, id);
    }
}
