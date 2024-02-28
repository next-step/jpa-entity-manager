package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuild;
import persistence.sql.dml.InsertQueryBuild;
import persistence.sql.dml.UpdateQueryBuild;
import persistence.sql.domain.Query;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    private final InsertQueryBuild insertQueryBuilder;

    private final UpdateQueryBuild updateQueryBuilder;

    private final DeleteQueryBuild deleteQueryBuilder;

    public EntityPersister(JdbcTemplate jdbcTemplate, InsertQueryBuild insertQueryBuilder, UpdateQueryBuild updateQueryBuilder, DeleteQueryBuild deleteQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertQueryBuilder = insertQueryBuilder;
        this.updateQueryBuilder = updateQueryBuilder;
        this.deleteQueryBuilder = deleteQueryBuilder;
    }

    public <T> boolean update(T entity, Object id) {
        Query query = updateQueryBuilder.update(entity, id);
        try {
            executeQuery(query);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public <T> void insert(T entity) {
        Query query = insertQueryBuilder.insert(entity);
        executeQuery(query);
    }

    public <T> void delete(T entity) {
        Query query = deleteQueryBuilder.delete(entity);
        executeQuery(query);
    }

    private void executeQuery(Query query) {
        jdbcTemplate.execute(query.getSql());
    }
}
