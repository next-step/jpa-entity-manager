package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.mapper.GenericRowMapper;

public class EntityLoaderImpl implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final SelectQueryBuilder selectQueryBuilder;


    public EntityLoaderImpl(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
        this.selectQueryBuilder = new SelectQueryBuilder(dialect);
    }

    @Override
    public <T> T find(Class<T> entity, Long id) {
        SelectQueryBuilder queryBuilder = selectQueryBuilder.build(entity);
        String query = queryBuilder.findById(id);
        return jdbcTemplate.queryForObject(query, new GenericRowMapper<T>(entity, dialect));
    }
}