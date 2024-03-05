package persistence.entity;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.sql.QueryException;
import persistence.sql.dml.*;
import persistence.sql.mapping.Column;
import persistence.sql.mapping.ColumnBinder;
import persistence.sql.mapping.Table;
import persistence.sql.mapping.TableBinder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class SingleEntityLoader implements EntityLoader {

    private final TableBinder tableBinder;
    private final DmlQueryBuilder dmlQueryBuilder;
    private final JdbcTemplate jdbcTemplate;

    public SingleEntityLoader(TableBinder tableBinder, DmlQueryBuilder dmlQueryBuilder, JdbcTemplate jdbcTemplate) {
        this.tableBinder = tableBinder;
        this.dmlQueryBuilder = dmlQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T load(final Class<T> clazz, final Object key) {
        final Table table = tableBinder.createTable(clazz);

        final Field idField = findIdField(clazz);
        final Column idColumn = table.getColumn(ColumnBinder.toColumnName(idField));
        idColumn.getValue().setValue(key);

        final Where where = new Where(idColumn, idColumn.getValue(), LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ));
        final Select select = new Select(table, List.of(where));

        final String selectQuery = dmlQueryBuilder.buildSelectQuery(select);
        final EntityRowMapper<T> entityRowMapper = new EntityRowMapper<>(clazz);

        return jdbcTemplate.queryForObject(selectQuery, entityRowMapper);
    }

    private <T> Field findIdField(final Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new QueryException("not found id column"));
    }
}
