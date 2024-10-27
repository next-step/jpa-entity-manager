package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.query.DeleteQuery;
import persistence.sql.dml.query.InsertQuery;
import persistence.sql.dml.query.UpdateQuery;
import persistence.sql.dml.query.builder.DeleteQueryBuilder;
import persistence.sql.dml.query.builder.InsertQueryBuilder;
import persistence.sql.dml.query.builder.UpdateQueryBuilder;

public class DefaultEntityPersister implements EntityPersister {

    @Override
    public <T> void insert(T entity, JdbcTemplate jdbcTemplate) {
        InsertQuery query = new InsertQuery(entity);
        String queryString = InsertQueryBuilder.builder()
                .insert(query.tableName(), query.columns())
                .values(query.columns())
                .build();
        jdbcTemplate.execute(queryString);
    }

    @Override
    public <T> void update(T entity, JdbcTemplate jdbcTemplate) {
        UpdateQuery query = new UpdateQuery(entity);
        String queryString = UpdateQueryBuilder.builder()
                        .update(query.tableName())
                        .set(query.columns())
                        .build();
        jdbcTemplate.execute(queryString);
    }

    @Override
    public <T> void delete(T entity, JdbcTemplate jdbcTemplate) {
        DeleteQuery query = new DeleteQuery(entity.getClass());
        String queryString = DeleteQueryBuilder.builder()
                .delete(query.tableName())
                .build();
        jdbcTemplate.execute(queryString);
    }

}
