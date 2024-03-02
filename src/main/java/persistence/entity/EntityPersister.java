package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuild;
import persistence.sql.dml.InsertQueryBuild;
import persistence.sql.dml.UpdateQueryBuild;
import persistence.sql.domain.Query;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    private final EntityInformation entityInformation;

    private final InsertQueryBuild insertQueryBuilder;

    private final UpdateQueryBuild updateQueryBuilder;

    private final DeleteQueryBuild deleteQueryBuilder;

    public EntityPersister(JdbcTemplate jdbcTemplate, InsertQueryBuild insertQueryBuilder, UpdateQueryBuild updateQueryBuilder, DeleteQueryBuild deleteQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityInformation = new EntityInformation();
        this.insertQueryBuilder = insertQueryBuilder;
        this.updateQueryBuilder = updateQueryBuilder;
        this.deleteQueryBuilder = deleteQueryBuilder;
    }

    public boolean update(Object entity) {
        Query query = updateQueryBuilder.update(entity);
        try {
            executeQuery(query);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public void insert(Object entity) {
        Query query = insertQueryBuilder.insert(entity);
        if (entityInformation.isNew(entity)) {
            Long id = jdbcTemplate.executeAndReturnGeneratedKey(query.getSql());
            entityInformation.setEntityId(entity, id);
            return;
        }
        executeQuery(query);
    }

    public void delete(Object entity) {
        Query query = deleteQueryBuilder.delete(entity);
        executeQuery(query);
    }

    private void executeQuery(Query query) {
        jdbcTemplate.execute(query.getSql());
    }


}
