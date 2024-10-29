package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.model.EntityFactory;
import persistence.model.EntityTable;
import persistence.sql.dml.DmlQueryBuilder;

public class EntityLoaderImpl implements EntityLoader {
    JdbcTemplate jdbcTemplate;
    DmlQueryBuilder dmlQueryBuilder;

    public EntityLoaderImpl(JdbcTemplate jdbcTemplate, DmlQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        EntityTable table = EntityFactory.createEmptySchema(clazz);
        table.setPrimaryValue(id);
        String tableName = table.getName();

        String selectQuery = dmlQueryBuilder.buildSelectByIdQuery(tableName, table.getPrimaryColumnKeyValue());
        return jdbcTemplate.queryForObject(
                selectQuery,
                resultSet -> new RowMapperImpl<>(clazz).mapRow(resultSet)
        );
    }
}
