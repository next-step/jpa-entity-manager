package persistence;

import jdbc.JdbcTemplate;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.UpdateQueryBuilder;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Object update(Object entity, Object id) {
        final UpdateQueryBuilder queryBuilder = new UpdateQueryBuilder(entity);
        final String query = queryBuilder.build(id);

        jdbcTemplate.execute(query);

        return entity;
    }

    public void insert(Object entity) {
        final InsertQueryBuilder queryBuilder = new InsertQueryBuilder(entity);
        final String query = queryBuilder.build();

        jdbcTemplate.execute(query);
    }

    public void delete(Object entity) {
        final DeleteQueryBuilder queryBuilder = new DeleteQueryBuilder(entity);
        final String query = queryBuilder.build();

        jdbcTemplate.execute(query);
    }

}
