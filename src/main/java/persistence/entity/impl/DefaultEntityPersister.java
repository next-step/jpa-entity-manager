package persistence.entity.impl;

import java.lang.reflect.Field;
import jdbc.JdbcTemplate;
import persistence.entity.EntityId;
import persistence.entity.EntityPersister;
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
    public <T> Object insert(T entity) {
        InsertQuery query = new InsertQuery(entity);
        String queryString = InsertQueryBuilder.builder()
                .insert(query.tableName(), query.columns())
                .values(query.columns())
                .build();
        Object id = jdbcTemplate.insertAndGetPrimaryKey(queryString);

        updateEntityId(entity, id);
        return entity;
    }


    private <T> void updateEntityId(T entity, Object id) {
        Field idField = EntityId.getIdField(entity);
        idField.setAccessible(true);
        try {
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
