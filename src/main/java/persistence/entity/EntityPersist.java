package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class EntityPersist {
    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public EntityPersist(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = new SelectQueryBuilder();
        this.insertQueryBuilder = new InsertQueryBuilder();
        this.deleteQueryBuilder = new DeleteQueryBuilder();
    }

    public void insert(Object entity) {
    }

    public boolean update() {
        return true;
    }

    public void delete(Object entity) {
        jdbcTemplate.execute(deleteQueryBuilder.generateSQL(entity));
    }
}
