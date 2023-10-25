package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.core.PersistenceEnvironment;
import persistence.exception.PersistenceException;
import persistence.sql.dml.DmlGenerator;

import java.sql.Connection;
import java.sql.SQLException;

public class EntityLoader<T> {
    private final PersistenceEnvironment persistenceEnvironment;
    private final EntityPersister entityPersister;
    private final DmlGenerator dmlGenerator;
    private final EntityRowMapper<T> entityRowMapper;

    public EntityLoader(final Class<T> clazz, final PersistenceEnvironment persistenceEnvironment, final EntityPersister entityPersister) {
        this.persistenceEnvironment = persistenceEnvironment;
        this.entityPersister = entityPersister;
        this.dmlGenerator = persistenceEnvironment.getDmlGenerator();
        this.entityRowMapper = new EntityRowMapper<>(clazz, entityPersister);
    }

    public T loadById(final Object id) {
        final String query = renderSelect(id);
        try (final Connection connection = persistenceEnvironment.getConnection()) {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
            return jdbcTemplate.queryForObject(query, entityRowMapper::mapRow);
        } catch (final SQLException e) {
            throw new PersistenceException("SQL 실행 중 오류가 발생했습니다.", e);
        }

    }

    public String renderSelect(final Object id) {
        return dmlGenerator.findById(entityPersister.getTableName(), entityPersister.getColumnNames(), entityPersister.getIdColumnName(), id);
    }
}
