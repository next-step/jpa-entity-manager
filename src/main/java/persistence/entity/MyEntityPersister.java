package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class MyEntityPersister implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public MyEntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(Object entity) {
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
        String query = updateQueryBuilder.build(entity);
        return jdbcTemplate.executeForUpdate(query);
    }

    public void insert(Object entity) {
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
        String query = insertQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }

    public void delete(Object entity) {
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
        String query = deleteQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }
}
