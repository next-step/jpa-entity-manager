package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.UpdateQueryBuilder;
import persistence.sql.dml.query.DeleteQuery;
import persistence.sql.dml.query.InsertQuery;
import persistence.sql.dml.query.UpdateQuery;

import java.sql.Connection;

public class EntityPersisterImpl implements EntityPersister{
    private final JdbcTemplate jdbcTemplate;

    public EntityPersisterImpl(Connection conn) {
        this.jdbcTemplate = new JdbcTemplate(conn);
    }

    @Override
    public <T> void insert(T entity) {
        InsertQuery query = new InsertQuery(entity);
        jdbcTemplate.execute(InsertQueryBuilder.builder()
                .insert(query.tableName(), query.columns())
                .values(query.columns())
                .build());
    }

    @Override
    public <T> void update(T entity) {
        UpdateQuery query = new UpdateQuery(entity);
        jdbcTemplate.execute(UpdateQueryBuilder.builder()
                .update(query.tableName())
                .set(query.columns())
                .build());
    }

    @Override
    public <T> void delete(T entity) {
        DeleteQuery query = new DeleteQuery(entity.getClass());
        jdbcTemplate.execute(DeleteQueryBuilder.builder()
                .delete(query.tableName())
                .build());
    }
}
