package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.QueryException;
import persistence.sql.dml.*;
import persistence.sql.mapping.Column;
import persistence.sql.mapping.PrimaryKey;
import persistence.sql.mapping.Table;
import persistence.sql.mapping.TableBinder;

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

        final Column idColumn = setIdColumn(key, table);

        final Where where = generateIdColumnWhere(idColumn);

        return new Select(table, List.of(where));
    }

    private Column setIdColumn(final Object key, final Table table) {
        final Column idColumn = findIdColumnInPrimaryKey(table.getPrimaryKey());
        idColumn.getValue().setValue(key);

        return idColumn;
    }

    private Where generateIdColumnWhere(final Column idColumn) {
        return new Where(idColumn, idColumn.getValue(), LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ));
    }

    private Column findIdColumnInPrimaryKey(final PrimaryKey primaryKey) {
        return primaryKey.getColumns()
                .stream()
                .findFirst()
                .orElseThrow(() -> new QueryException("not found id column"));
    }
}
