package persistence.entity.impl;

import java.util.List;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityManager;
import persistence.entity.EntityRowMapperFactory;
import persistence.sql.QueryBuilder;

public class EntityManagerImpl implements EntityManager {
    private static final Logger log = LoggerFactory.getLogger(EntityManagerImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private final QueryBuilder queryBuilder;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, new QueryBuilder());
    }

    public EntityManagerImpl(JdbcTemplate jdbcTemplate, QueryBuilder queryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public <T> T find(Class<T> entityClass, Long id) {
        String selectByIdQuery = queryBuilder.getSelectByIdQuery(entityClass, id);

        log.info("SQL: {}", selectByIdQuery);

        return jdbcTemplate.queryForObject(
            selectByIdQuery,
            EntityRowMapperFactory.getInstance().getRowMapper(entityClass)
        );
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        String selectAllQuery = queryBuilder.getSelectAllQuery(entityClass);

        log.info("SQL: {}", selectAllQuery);

        return jdbcTemplate.query(
            selectAllQuery,
            EntityRowMapperFactory.getInstance().getRowMapper(entityClass)
        );
    }

    @Override
    public Object persist(Object entity) {
        String insertQuery = queryBuilder.getInsertQuery(entity);

        log.info("SQL: {}", insertQuery);

        jdbcTemplate.execute(insertQuery);

        return entity;
    }

    @Override
    public void remove(Object entity) {
        String deleteQueryFromEntity = queryBuilder.getDeleteQueryFromEntity(entity);

        log.info("SQL: {}", deleteQueryFromEntity);

        jdbcTemplate.execute(deleteQueryFromEntity);
    }
}
