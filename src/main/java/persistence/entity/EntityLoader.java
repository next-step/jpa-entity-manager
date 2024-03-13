package persistence.entity;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.conditions.WhereRecord;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.PrimaryKeyMetadata;

import java.util.List;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;

    public EntityLoader(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public <T> T find(EntityId entityId) {
        Class<?> clazz = entityId.getClazz();

        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.builder()
                .dialect(dialect)
                .entity(clazz)
                .where(List.of(WhereRecord.of(String.valueOf(entityId.getName()), "=", entityId.getValue())))
                .build();

        return jdbcTemplate.queryForObject(selectQueryBuilder.generateQuery(), resultSet -> (T) new EntityRowMapper<>(clazz).mapRow(resultSet));
    }

    public <T> T findByEntity(T entity) {
        Class<T> clazz = (Class<T>) entity.getClass();
        EntityMetadata metadata = EntityMetadata.of(clazz, entity);
        PrimaryKeyMetadata primaryKey = metadata.getPrimaryKey();

        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.builder()
                .dialect(dialect)
                .entity(clazz)
                .where(List.of(WhereRecord.of(primaryKey.getName(), "=", primaryKey.getValue())))
                .build();

        return jdbcTemplate.queryForObject(selectQueryBuilder.generateQuery(), resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));
    }
}
