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

    private final String entityName;
    private final EntityTable entityTable;
    private final EntityColumns entityColumns;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(Class<?> clazz, Connection connection) {
        this.entityName = clazz.getSimpleName();
        this.entityTable = EntityTable.from(clazz);
        this.entityColumns = EntityColumns.from(clazz);
        this.jdbcTemplate = new JdbcTemplate(connection);

    }

    public boolean update(Object entity) {
        try {
            UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(entityTable, entityColumns);
            String updateQuery = updateQueryBuilder.update(entity, getIdValue(entity));
            jdbcTemplate.execute(updateQuery);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void insert(Object entity) {
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(entityTable, entityColumns);
        String insertQuery = insertQueryBuilder.getInsertQuery(entity);
        jdbcTemplate.execute(insertQuery);
    }

    public void delete(Object entity) {
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
        String deleteQuery = deleteQueryBuilder.delete(entity.getClass(), getIdValue(entity));
        jdbcTemplate.execute(deleteQuery);
    }

    public <T> T select(Class<T> clazz, Long id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz);
        String selectQuery = selectQueryBuilder.findById(clazz, id);
        return jdbcTemplate.queryForObject(selectQuery, new EntityRowMapper<>(clazz));
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
