package hibernate.entity;

import hibernate.dml.SelectQueryBuilder;
import jdbc.JdbcTemplate;
import jdbc.ReflectionRowMapper;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;

    public EntityLoader(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T find(final EntityClass<T> entityClass, final Object id) {
        final String query = selectQueryBuilder.generateQuery(
                entityClass.tableName(),
                entityClass.getFieldNames(),
                entityClass.getEntityId(),
                id
        );
        return jdbcTemplate.queryForObject(query, ReflectionRowMapper.getInstance(entityClass));
    }
}
