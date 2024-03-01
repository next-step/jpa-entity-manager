package persistence.core;

import database.DatabaseServer;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.entity.metadata.EntityColumn;
import persistence.entity.metadata.EntityMetadata;
import persistence.inspector.EntityInfoExtractor;
import persistence.sql.dml.DMLQueryBuilder;

import java.lang.reflect.Field;
import java.sql.SQLException;

public class EntityPersister extends EntityContextManager {

    EntityMetadata entityMetadata;
    DMLQueryBuilder dmlQueryBuilder;
    JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate, Class<?> clazz) throws SQLException {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetadata = getEntityMetadata(clazz);
        this.dmlQueryBuilder = DMLQueryBuilder.getInstance();
    }

    public void insert(Object entity) {
        String sql = dmlQueryBuilder.insertSql(entity);
        jdbcTemplate.execute(sql);
    }

    public <T> T select(Class<?> entityClass, Long id) throws Exception {
        String sql = dmlQueryBuilder.selectByIdQuery(entityClass, id);
        Object o = jdbcTemplate.queryForObject(sql, entityMetadata.getRowMapper());

        return (T) o;
    }

    public boolean update(Object entity) throws Exception {
        Object idValue = getIdValue(entity);

        try {
            select(entity.getClass(), (Long) idValue);
        } catch (Exception e) {
            throw e;
        }

        String sql = dmlQueryBuilder.updateSql(entity);
        jdbcTemplate.execute(sql);

        return true;
    }

    public void delete(Object entity) throws Exception {
        Object idValue = getIdValue(entity);
        try {
            select(entity.getClass(), (Long) idValue);
        } catch (Exception e) {
            throw e;
        }

        String sql = dmlQueryBuilder.deleteSql(entity);
        jdbcTemplate.execute(sql);
        // Delete the entity from the database
    }

    // todo RowMapper를 어떻게 구현한담..
    private RowMapper getRowMapper(Class<?> clazz) throws Exception {
        Object entity = clazz.getDeclaredConstructor(String.class, int.class).newInstance();

        RowMapper<Object> rowMapper = resultSet -> {
            for (EntityColumn column : entityMetadata.getColumns().getColumns()) {
                setEntityValues(entity, column.getField(), resultSet.getObject(column.getColumnName()));
            }
            return entity;
        };

        return rowMapper;
    }

    private void setEntityValues(Object instance, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(instance, value);
    }

    private Object getEntityValue(Object instance, Field field) {
        return EntityInfoExtractor.getFieldValue(instance, field);
    }

    private Object getIdValue(Object entity) {
        return getEntityValue(entity, entityMetadata.getColumns().getIdColumn().getField());
    }
}
