package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.model.EntityFactory;
import persistence.model.EntityTable;
import persistence.sql.dml.DmlQueryBuilder;

import java.util.List;

public class EntityLoaderImpl implements EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryBuilder dmlQueryBuilder;

    public EntityLoaderImpl(JdbcTemplate jdbcTemplate, DmlQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        String selectQuery = buildDefaultSelectQuery(clazz, id);

        return jdbcTemplate.queryForObject(
                selectQuery,
                resultSet -> new RowMapperImpl<>(clazz).mapRow(resultSet)
        );
    }

    @Override
    public <T> boolean exists(Class<T> clazz, Object id) {
        String selectQuery = buildDefaultSelectQuery(clazz, id);

        List<T> queryResult = jdbcTemplate.query(
                selectQuery,
                resultSet -> new RowMapperImpl<>(clazz).mapRow(resultSet)
        );
        return !queryResult.isEmpty();
    }

    private <T> String buildDefaultSelectQuery(Class<T> clazz, Object id) {
        EntityTable table = EntityFactory.createEmptySchema(clazz);
        table.setPrimaryValue(id);

        return dmlQueryBuilder.buildSelectByIdQuery(table.getName(), table.getPrimaryColumnKeyValue());
    }
}
