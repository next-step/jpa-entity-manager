package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;
    private final DmlGenerator dmlGenerator;

    public EntityPersister(final JdbcTemplate jdbcTemplate, final DmlGenerator dmlGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = dmlGenerator;
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
        return dmlGenerator.findById(clazz, id);
    }

    public String renderInsert(final Object entity) {
        return dmlGenerator.insert(entity);
    }

    public String renderUpdate(final Object entity) {
        return dmlGenerator.update(entity);
    }

    public String renderDelete(final Object entity) {
        return dmlGenerator.delete(entity);
    }

}
