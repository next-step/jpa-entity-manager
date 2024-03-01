package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class MyEntityPersister implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public MyEntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertQueryBuilder = InsertQueryBuilder.getInstance();
        this.updateQueryBuilder = UpdateQueryBuilder.getInstance();
        this.deleteQueryBuilder = DeleteQueryBuilder.getInstance();
    }

    @Override
    public boolean update(Object entity) {
        String query = updateQueryBuilder.build(entity);
        return jdbcTemplate.executeForUpdate(query);
    }

    @Override
    public Object insert(Object entity) {
        String query = insertQueryBuilder.build(entity);
        return jdbcTemplate.executeForInsert(query);
    }

    @Override
    public void delete(Object entity) {
        String query = deleteQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }
}
