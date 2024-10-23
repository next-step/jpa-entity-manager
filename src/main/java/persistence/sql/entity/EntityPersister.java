package persistence.sql.entity;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.sql.Metadata;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

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

    public void insert(Object entity) throws SQLException {
        String insertQuery = insertQueryBuilder.getInsertQuery(entityTable, entityColumns, entity);
        Long idValue = jdbcTemplate.insertAndReturnId(insertQuery);

        setEntityIdValue(entity, idValue);
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

    public void setIdValue(Object entity, Long idValue) {
        Class<?> clazz = entity.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    field.set(entity, idValue);  // ID 값을 엔티티의 필드에 설정
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("ID 값을 설정하는 중 오류 발생", e);
                }
            }
        }
    }

    private void setEntityIdValue(Object entity, Long idValue) {
        Class<?> clazz = entity.getClass();
        Metadata metadata = new Metadata(clazz);
        try {
            Field declaredField = clazz.getDeclaredField(metadata.getIdFieldName());
            declaredField.setAccessible(true);
            declaredField.set(entity, idValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
