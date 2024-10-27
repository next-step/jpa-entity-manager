package persistence.entity;

import persistence.sql.dml.query.DeleteQuery;
import persistence.sql.dml.query.InsertQuery;
import persistence.sql.dml.query.builder.DeleteQueryBuilder;
import persistence.sql.dml.query.builder.InsertQueryBuilder;

public class DefaultEntityPersister implements EntityPersister {

    @Override
    public <T> void insert(T entity, EntityManager entityManager) {
        InsertQuery query = new InsertQuery(entity);
        String queryString = InsertQueryBuilder.builder(entityManager.getDialect())
                .insert(query.tableName(), query.columns())
                .values(query.columns())
                .build();
        entityManager.execute(queryString);
    }

    @Override
    public <T> void update(T entity, EntityManager entityManager) {

    }

    @Override
    public <T> void delete(T entity, EntityManager entityManager) {
        DeleteQuery query = new DeleteQuery(entity.getClass());
        String queryString = DeleteQueryBuilder.builder(entityManager.getDialect())
                .delete(query.tableName())
                .build();
        entityManager.execute(queryString);
    }

}
