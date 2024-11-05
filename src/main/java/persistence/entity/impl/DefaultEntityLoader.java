package persistence.entity.impl;

import java.util.List;
import jdbc.JdbcTemplate;
import persistence.entity.EntityLoader;
import persistence.sql.dml.query.SelectQuery;
import persistence.sql.dml.query.WhereCondition;
import persistence.sql.dml.query.builder.SelectQueryBuilder;

public class DefaultEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;

    public DefaultEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T load(Class<T> clazz, Object id) {
        SelectQuery query = new SelectQuery(clazz);
        String queryString = SelectQueryBuilder.builder()
                .select(query.columnNames())
                .from(query.tableName())
                .where(List.of(new WhereCondition("id", "=", id)))
                .build();
        return jdbcTemplate.queryForObject(queryString, new EntityRowMapper<>(clazz));
    }
}
