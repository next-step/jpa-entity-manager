package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

public class EntityLoader<T> {
    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;
    private final EntityRowMapper<T> entityRowMapper;

    public EntityLoader(final Class<T> clazz, final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate, final EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
        this.dmlGenerator = dmlGenerator;
        this.jdbcTemplate = jdbcTemplate;
        this.entityRowMapper = new EntityRowMapper<>(clazz, entityPersister);
    }

    public T loadById(final Object id) {
        final String query = renderSelect(id);
        return jdbcTemplate.queryForObject(query, entityRowMapper::mapRow);
    }

    public String renderSelect(final Object id) {
        return dmlGenerator.findById(entityPersister.getTableName(), entityPersister.getColumnNames(), entityPersister.getIdColumnName(), id);
    }
}
