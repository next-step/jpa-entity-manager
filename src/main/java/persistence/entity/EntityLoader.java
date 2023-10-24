package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

public class EntityLoader<T> {

    private final EntityPersister entityPersister;
    private final JdbcTemplate jdbcTemplate;
    private final DmlGenerator dmlGenerator;
    private final EntityRowMapper<T> entityRowMapper;

    public EntityLoader(final Class<T> clazz, final EntityPersister entityPersister, final JdbcTemplate jdbcTemplate, final DmlGenerator dmlGenerator) {
        this.entityPersister = entityPersister;
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = dmlGenerator;
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
