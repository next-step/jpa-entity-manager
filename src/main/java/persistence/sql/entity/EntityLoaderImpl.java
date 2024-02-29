package persistence.sql.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.meta.Columns;
import persistence.sql.meta.PrimaryKey;

import static persistence.sql.entity.RowMapperFactory.createRowMapper;

public class EntityLoaderImpl implements EntityLoader {

    private final String tableName;
    private final PrimaryKey primaryKey;
    private final Columns columns;
    private final JdbcTemplate jdbcTemplate;

    public EntityLoaderImpl(String tableName, PrimaryKey primaryKey, Columns columns, JdbcTemplate jdbcTemplate) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.columns = columns;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T findById(final Class<T> clazz, final Long id) {
        final SelectQueryBuilder queryBuilder = new SelectQueryBuilder(tableName, primaryKey, columns);
        final String findByIdQuery = queryBuilder.createFindByIdQuery(id);

        return jdbcTemplate.queryForObject(findByIdQuery, createRowMapper(clazz));
    }
}
