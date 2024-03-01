package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.sql.dml.BooleanExpression;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.WhereBuilder;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final DynamicRowMapperFactory rowMapperFactory;
    public EntityLoader(JdbcTemplate jdbcTemplate, DynamicRowMapperFactory rowMapperFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapperFactory = rowMapperFactory;
    }

    public <T> T find(Class<T> clazz, Long id) {
        TableData table = TableData.from(clazz);
        Columns columns = Columns.createColumns(clazz);
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(table, columns);
        WhereBuilder whereBuilder = new WhereBuilder();
        whereBuilder.and(BooleanExpression.eq(columns.getKeyColumnName(), id));

        String query = selectQueryBuilder.build(whereBuilder);
        RowMapper<T> rowMapper = rowMapperFactory.create(clazz);
        try {
            return jdbcTemplate.queryForObject(query, rowMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
