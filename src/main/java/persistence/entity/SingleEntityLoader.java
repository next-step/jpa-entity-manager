package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.QueryException;
import persistence.sql.dml.*;
import persistence.sql.mapping.*;

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
        final Select select = generateSelect(clazz, key);

        final String selectQuery = dmlQueryBuilder.buildSelectQuery(select);
        final EntityRowMapper<T> entityRowMapper = new EntityRowMapper<>(clazz);

        return jdbcTemplate.queryForObject(selectQuery, entityRowMapper);
    }

    private <T> Select generateSelect(final Class<T> clazz, final Object key) {
        final Table table = tableBinder.createTable(clazz);

        final Where where = generateIdColumnWhere(table, key);

        return new Select(table, List.of(where));
    }

    private Where generateIdColumnWhere(final Table table, final Object key) {
        final Column idColumn = findIdColumnInPrimaryKey(table.getPrimaryKey());
        idColumn.setValue(key);

        return new Where(idColumn, idColumn.getValue(), LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ));
    }

    private Column findIdColumnInPrimaryKey(final PrimaryKey primaryKey) {
        return primaryKey.getColumns()
                .stream()
                .findFirst()
                .orElseThrow(() -> new QueryException("not found id column"));
    }
}
