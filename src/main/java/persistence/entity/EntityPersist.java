package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.UpdateQueryBuilder;

public class EntityPersist {
    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public EntityPersist(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertQueryBuilder = new InsertQueryBuilder();
        this.updateQueryBuilder = new UpdateQueryBuilder();
        this.deleteQueryBuilder = new DeleteQueryBuilder();
    }

    public <T> void insert(T entity) {
        jdbcTemplate.execute(insertQueryBuilder.generateSQL(entity));
    }

    public <T> void update(Long pk, T entity) {
        String sql = updateQueryBuilder.generateSQL(pk, entity);
        jdbcTemplate.execute(sql);
    }

    public <T> void delete(T entity) {
        jdbcTemplate.execute(deleteQueryBuilder.generateSQL(entity));
    }
}
