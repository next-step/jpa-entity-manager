package persistence.sql.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQueryBuilder;

import java.sql.Connection;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
    }

    public <T> T loadEntity(Class<T> clazz, Long id) {
        EntityTable entityTable = EntityTable.from(clazz);
        EntityColumns entityColumns = EntityColumns.from(clazz);
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();

        String selectQuery = selectQueryBuilder.findById(entityTable, entityColumns, id);
        return jdbcTemplate.queryForObject(selectQuery, new EntityRowMapper<>(clazz));
    }
}
