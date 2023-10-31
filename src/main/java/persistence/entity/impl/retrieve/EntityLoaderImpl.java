package persistence.entity.impl.retrieve;

import java.sql.Connection;
import jdbc.JdbcTemplate;
import persistence.entity.impl.EntityRowMapper;
import persistence.sql.dialect.ColumnType;
import persistence.sql.dml.clause.WherePredicate;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.statement.SelectStatementBuilder;
import persistence.sql.schema.EntityClassMappingMeta;

public class EntityLoaderImpl implements EntityLoader {

    private final ColumnType columnType;
    private final JdbcTemplate jdbcTemplate;

    public EntityLoaderImpl(Connection connection, ColumnType columnType) {
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.columnType = columnType;
    }

    @Override
    public <T> T load(Class<T> clazz, Object id) {
        final EntityClassMappingMeta classMappingMeta = EntityClassMappingMeta.of(clazz, columnType);

        final String selectSql = SelectStatementBuilder.builder()
            .select(clazz, columnType)
            .where(WherePredicate.of(classMappingMeta.getIdFieldColumnName(), id, new EqualOperator()))
            .build();

        return jdbcTemplate.queryForObject(selectSql, new EntityRowMapper<>(clazz, columnType));
    }
}
