package hibernate.entity;

import hibernate.dml.SelectAllQueryBuilder;
import hibernate.dml.SelectQueryBuilder;
import hibernate.entity.column.EntityColumns;
import jdbc.JdbcTemplate;
import jdbc.ReflectionRowMapper;

public class EntityLoader<T> {

    private final Class<T> clazz;
    private final EntityTableName entityTableName;
    private final EntityColumns entityColumns;
    private final JdbcTemplate jdbcTemplate;
    private final SelectAllQueryBuilder selectAllQueryBuilder = SelectAllQueryBuilder.INSTANCE;
    private final SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;

    public EntityLoader(final Class<T> clazz, final JdbcTemplate jdbcTemplate) {
        this.clazz = clazz;
        this.entityTableName = new EntityTableName(clazz);
        this.entityColumns = new EntityColumns(clazz.getDeclaredFields());
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T find(final Class<T> clazz, final Object id) {
        final String query = selectQueryBuilder.generateQuery(
                entityTableName.getTableName(),
                entityColumns.getFieldNames(),
                entityColumns.getEntityId(),
                id
        );
        return jdbcTemplate.queryForObject(query, new ReflectionRowMapper<>(clazz));
    }
}
