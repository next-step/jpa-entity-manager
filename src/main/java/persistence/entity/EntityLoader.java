package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQueryBuild;
import persistence.sql.domain.Query;
import persistence.sql.domain.QueryResult;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;

    private final SelectQueryBuild selectQueryBuilder;

    public EntityLoader(JdbcTemplate jdbcTemplate,
                        SelectQueryBuild selectQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = selectQueryBuilder;
    }

    public <T> T find(Class<T> clazz, Object id) {
        Query query = selectQueryBuilder.findById(clazz, id);

        return jdbcTemplate.queryForObject(query.getSql(), new QueryResult<>(query.getTable(), clazz));
    }

}
