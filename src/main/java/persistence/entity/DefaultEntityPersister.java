package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.query.DeleteQuery;
import persistence.sql.dml.query.InsertQuery;
import persistence.sql.dml.query.UpdateQuery;
import persistence.sql.dml.query.builder.DeleteQueryBuilder;
import persistence.sql.dml.query.builder.InsertQueryBuilder;
import persistence.sql.dml.query.builder.UpdateQueryBuilder;

public class DefaultEntityPersister implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public DefaultEntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> void insert(T entity) {
        InsertQuery query = new InsertQuery(entity);
        String queryString = InsertQueryBuilder.builder()
                .insert(query.tableName(), query.columns())
                .values(query.columns())
                .build();
        jdbcTemplate.execute(queryString);
    }

    @Override
    public <T> void update(T entity) {
        UpdateQuery query = new UpdateQuery(entity);
        String queryString = UpdateQueryBuilder.builder()
                        .update(query.tableName())
                        .set(query.columns())
                        .build();
        jdbcTemplate.execute(queryString);
    }

    @Override
    public <T> void delete(T entity) {
        DeleteQuery query = new DeleteQuery(entity.getClass());
        String queryString = DeleteQueryBuilder.builder()
                .delete(query.tableName())
                .build();
        jdbcTemplate.execute(queryString);
    }

}
