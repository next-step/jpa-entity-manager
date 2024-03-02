package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.Delete;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.dml.Insert;
import persistence.sql.dml.Update;
import persistence.sql.mapping.Table;
import persistence.sql.mapping.TableBinder;

public class SingleTableEntityPersister implements EntityPersister {

    private final TableBinder tableBinder;
    private final DmlQueryBuilder dmlQueryBuilder;
    private final JdbcTemplate jdbcTemplate;

    public SingleTableEntityPersister(TableBinder tableBinder, DmlQueryBuilder dmlQueryBuilder, JdbcTemplate jdbcTemplate) {
        this.tableBinder = tableBinder;
        this.dmlQueryBuilder = dmlQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean update(final Object entity) {
        final Table table = tableBinder.createTable(entity);
        final String updateQuery = dmlQueryBuilder.buildUpdateQuery(new Update(table));

        try {
            jdbcTemplate.execute(updateQuery);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public void insert(final Object entity) {
        final Table table = tableBinder.createTable(entity);

        final String insertQuery = dmlQueryBuilder.buildInsertQuery(new Insert(table));

        jdbcTemplate.execute(insertQuery);
    }

    @Override
    public void delete(final Object entity) {
        final Table table = tableBinder.createTable(entity);

        final String deleteQuery = dmlQueryBuilder.buildDeleteQuery(new Delete(table));

        jdbcTemplate.execute(deleteQuery);
    }
}
