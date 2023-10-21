package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

import static persistence.entity.EntityQueryType.*;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;
    private final DmlGenerator dmlGenerator;
    private final EntityQueryCache<Object> queryCache;

    public EntityPersister(final JdbcTemplate jdbcTemplate, final DmlGenerator dmlGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = dmlGenerator;
        this.queryCache = new EntityQueryCache<>();
    }

    public void insert(final Object entity) {
        final String insertQuery = renderInsert(entity);
        jdbcTemplate.execute(insertQuery);
    }

    public void update(final Object entity) {
        final String updateQuery = renderUpdate(entity);
        jdbcTemplate.execute(updateQuery);
    }

    public void delete(final Object entity) {
        final String deleteQuery = renderDelete(entity);
        jdbcTemplate.execute(deleteQuery);
    }

    public String renderSelect(final Class<?> clazz, final Long id) {
        return queryCache.computeIfAbsent(SELECT, id, object -> dmlGenerator.findById(clazz, id));
    }

    public String renderInsert(final Object entity) {
        return queryCache.computeIfAbsent(INSERT, entity, object -> dmlGenerator.insert(entity));
    }

    public String renderUpdate(final Object entity) {
        return queryCache.computeIfAbsent(UPDATE, entity, object -> dmlGenerator.update(entity));
    }

    public String renderDelete(final Object entity) {
        return queryCache.computeIfAbsent(DELETE, entity, object -> dmlGenerator.delete(entity));
    }

}
