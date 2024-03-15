package persistence.entity.impl;

import java.util.List;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityLoader;
import persistence.entity.EntityRowMapperFactory;
import persistence.sql.QueryBuilder;

public class EntityLoaderImpl implements EntityLoader {
    private static final Logger log = LoggerFactory.getLogger(EntityLoaderImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private final QueryBuilder queryBuilder;

    public EntityLoaderImpl(JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, new QueryBuilder());
    }

    public EntityLoaderImpl(JdbcTemplate jdbcTemplate, QueryBuilder queryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public <T> T select(Class<T> entityClass, Object id) {
        String selectByIdQuery = queryBuilder.getSelectByIdQuery(entityClass, id);

        log.info("Entity selected successfully. SQL: {}", selectByIdQuery);

        RowMapper<T> rowMapper = EntityRowMapperFactory.getInstance().getRowMapper(entityClass);

        return jdbcTemplate.queryForObject(selectByIdQuery, rowMapper);
    }

    @Override
    public <T> List<T> selectAll(Class<T> entityClass) {
        String selectAllQuery = queryBuilder.getSelectAllQuery(entityClass);

        log.info("Entity selected all successfully. SQL: {}", selectAllQuery);

        RowMapper<T> rowMapper = EntityRowMapperFactory.getInstance().getRowMapper(entityClass);

        return jdbcTemplate.query(
            selectAllQuery,
            rowMapper
        );
    }

    @Override
    public boolean isExists(Class<?> entityClass, Object id) {
        if (id == null) {
            return false;
        }
        
        String selectByIdQuery = queryBuilder.getSelectByIdQuery(entityClass, id);

        log.info("SQL: {}", selectByIdQuery);

        RowMapper<?> rowMapper = EntityRowMapperFactory.getInstance().getRowMapper(entityClass);

        Object object = jdbcTemplate.queryForObjectWithoutException(selectByIdQuery, rowMapper);

        return object != null;
    }
}
