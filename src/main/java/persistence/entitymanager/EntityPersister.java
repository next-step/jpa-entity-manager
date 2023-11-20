package persistence.entitymanager;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void update(Object entity) {
        String query = UpdateQueryBuilder.getQuery(entity);
        jdbcTemplate.execute(query);
    }

    public void insert(Object entity) {
        String query = InsertQueryBuilder.getQuery(entity);
        jdbcTemplate.execute(query);
    }

    public void delete(Object entity) {
        String query = DeleteQueryBuilder.getQuery(entity);
        jdbcTemplate.execute(query);
    }
}
