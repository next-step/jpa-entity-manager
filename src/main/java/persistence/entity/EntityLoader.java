package persistence.entity;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.conditions.WhereRecord;
import persistence.sql.metadata.EntityMetadata;

import java.util.List;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;

    public EntityLoader(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public <T> T find(Class<T> clazz, Object id) {
        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.builder()
                .dialect(dialect)
                .entity(clazz)
                .where(List.of(WhereRecord.of(EntityMetadata.from(clazz).getPrimaryKey().getName(), "=", id)))
                .build();

        return jdbcTemplate.queryForObject(selectQueryBuilder.generateQuery(), resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));
    }
}
