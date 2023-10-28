package persistence.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;


public class EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(EntityPersister.class);
    private final JdbcTemplate jdbcTemplate;
    private final EntityMeta entityMeta;

    private final DeleteQueryBuilder deleteQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;

    public EntityPersister(JdbcTemplate jdbcTemplate,
                           EntityMeta entityMeta,
                           QueryGenerator queryGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMeta = entityMeta;
        this.deleteQueryBuilder = queryGenerator.delete();
        this.insertQueryBuilder = queryGenerator.insert();
        this.updateQueryBuilder = queryGenerator.update();
    }

    public <T> T insert(T entity) {
        final String query = insertQueryBuilder.build(entity);
        log.info(query);

        if (entityMeta.isAutoIncrement()) {
            final long id = jdbcTemplate.insertForGenerateKey(query);
            changeValue(entityMeta.getPkColumn(), entity, id);
        }
        return (T) newEntity(entity);
    }


    public boolean update(Object entity) {
        final String query = updateQueryBuilder.build(entity);
        log.info(query);
        jdbcTemplate.execute(query);
        return true;
    }

    public void delete(Object entity) {
        Object id = entityMeta.getPkValue(entity);
        final String query = deleteQueryBuilder.build(id);
        log.info(query);
        jdbcTemplate.execute(query);

    }

    private Object newEntity(Object entity) {
        try {
            Object snapShot = entity.getClass().getDeclaredConstructor().newInstance();
            copyColumns(entity, snapShot);
            return snapShot;
        } catch (InstantiationException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyColumns(Object entity, Object snapShot) throws NoSuchFieldException, IllegalAccessException {
        for (EntityColumn entityColumn : entityMeta.getEntityColumns()) {
            final String fieldName = entityColumn.getFieldName();
            final Object value = entityColumn.getFieldValue(entity);
            final Field declaredField = entity.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(snapShot, value);
        }
    }

    private void changeValue(EntityColumn column, Object entity, Object value) {
        try {
            final Field field = entity.getClass().getDeclaredField(column.getFieldName());
            field.setAccessible(true);
            field.set(entity, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
