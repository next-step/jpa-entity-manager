package persistence.entity;

import java.lang.reflect.Field;
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

    private final PersistenceContext persistenceContext;


    public EntityPersister(PersistenceContext persistenceContext, JdbcTemplate jdbcTemplate, EntityMeta entityMeta, QueryGenerator queryGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMeta = entityMeta;
        this.persistenceContext = persistenceContext;
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
            persistenceContext.getDatabaseSnapshot(entityMeta.getPkValue(entity), entity);
            return entity;
        }
        jdbcTemplate.execute(query);
        persistenceContext.getDatabaseSnapshot(entityMeta.getPkValue(entity), entity);
        return entity;
    }

    public boolean update(Object entity) {
        final String query = updateQueryBuilder.build(entity);
        log.info(query);
        jdbcTemplate.execute(query);
        return true;
    }

    public void delete(Object entity) {
        final String query = deleteQueryBuilder.build(entityMeta.getPkValue(entity));
        log.info(query);
        jdbcTemplate.execute(query);
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
