package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

import java.util.HashMap;
import java.util.Map;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;
    private final DmlGenerator dmlGenerator;
    private final Map<Object, String> insertQueryCache;
    private final Map<Object, String> updateQueryCache;
    private final Map<Object, String> deleteQueryCache;
    private final Map<Long, String> selectQueryCache;

    public EntityPersister(final JdbcTemplate jdbcTemplate, final DmlGenerator dmlGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = dmlGenerator;
        this.insertQueryCache = new HashMap<>();
        this.updateQueryCache = new HashMap<>();
        this.deleteQueryCache = new HashMap<>();
        this.selectQueryCache = new HashMap<>();
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
        return selectQueryCache.computeIfAbsent(id, object -> dmlGenerator.findById(clazz, id));
    }

    public String renderInsert(final Object entity) {
        return insertQueryCache.computeIfAbsent(entity, object -> dmlGenerator.insert(entity));
    }

    public String renderUpdate(final Object entity) {
        return updateQueryCache.computeIfAbsent(entity, object -> dmlGenerator.update(entity));
    }

    public String renderDelete(final Object entity) {
        return deleteQueryCache.computeIfAbsent(entity, object -> dmlGenerator.delete(entity));
    }

}
