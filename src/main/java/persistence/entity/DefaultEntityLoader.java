package persistence.entity;

import java.util.List;
import java.util.Optional;
import jdbc.JdbcTemplate;
import persistence.sql.dml.query.SelectQuery;
import persistence.sql.dml.query.WhereCondition;
import persistence.sql.dml.query.builder.SelectQueryBuilder;

public class DefaultEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final PersistenceContext context;

    public DefaultEntityLoader(JdbcTemplate jdbcTemplate, PersistenceContext context) {
        this.jdbcTemplate = jdbcTemplate;
        this.context = context;
    }

    @Override
    public <T> T load(Class<T> clazz, Object id) {
        Optional<T> entity = context.getEntity(id, clazz);
        if (entity.isPresent()) {
            return entity.get();
        }

        SelectQuery query = new SelectQuery(clazz);
        String queryString = SelectQueryBuilder.builder()
                .select(query.columnNames())
                .from(query.tableName())
                .where(List.of(new WhereCondition("id", "=", id)))
                .build();
        return jdbcTemplate.queryForObject(queryString, new EntityRowMapper<>(clazz));
    }
}
