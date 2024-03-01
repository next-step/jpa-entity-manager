package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuild;
import persistence.sql.dml.InsertQueryBuild;
import persistence.sql.dml.UpdateQueryBuild;
import persistence.sql.domain.DatabasePrimaryColumn;
import persistence.sql.domain.DatabaseTable;
import persistence.sql.domain.Query;

import java.lang.reflect.Field;

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
        DatabasePrimaryColumn primaryColumn = new DatabaseTable(entity).getPrimaryColumn();
        Query query = insertQueryBuilder.insert(entity);
        if (primaryColumn.hasColumnValue()){
            jdbcTemplate.execute(query.getSql());
            return;
        }
        Long id = jdbcTemplate.executeAndReturnGeneratedKey(query.getSql());
        setEntityId(entity, id);
    }

    private void setEntityId(Object entity, Object id) {
        DatabasePrimaryColumn primaryColumn = new DatabaseTable(entity).getPrimaryColumn();
        try {
            Field declaredField = entity.getClass().getDeclaredField(primaryColumn.getJavaFieldName());
            declaredField.setAccessible(true);
            declaredField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public void delete(Object entity) {
        Query query = deleteQueryBuilder.delete(entity);
        executeQuery(query);
    }

    private void executeQuery(Query query) {
        jdbcTemplate.execute(query.getSql());
    }


}
