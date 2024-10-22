package persistence.sql.entity;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import java.lang.reflect.Field;
import java.sql.Connection;

public class EntityPersister {

    private final EntityTable entityTable;
    private final EntityColumns entityColumns;
    private final JdbcTemplate jdbcTemplate;

    private final UpdateQueryBuilder updateQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public EntityPersister(Class<?> clazz, Connection connection) {
        this.entityTable = EntityTable.from(clazz);
        this.entityColumns = EntityColumns.from(clazz);
        this.jdbcTemplate = new JdbcTemplate(connection);

        this.updateQueryBuilder = new UpdateQueryBuilder();
        this.insertQueryBuilder = new InsertQueryBuilder();
        this.deleteQueryBuilder = new DeleteQueryBuilder();
    }

    public boolean update(Object entity) {
        try {
            String updateQuery = updateQueryBuilder.update(entityTable, entityColumns, entity, getIdValue(entity));
            jdbcTemplate.execute(updateQuery);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void insert(Object entity) {
        String insertQuery = insertQueryBuilder.getInsertQuery(entityTable, entityColumns, entity);
        jdbcTemplate.execute(insertQuery);
    }

    public void delete(Object entity) {
        String deleteQuery = deleteQueryBuilder.delete(entityTable, entityColumns, getIdValue(entity));
        jdbcTemplate.execute(deleteQuery);
    }

    public Long getIdValue(Object entity) {
        Class<?> clazz = entity.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    return (Long) field.get(entity);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("id값이 없음");
                }
            }
        }
        return null;
    }

}
