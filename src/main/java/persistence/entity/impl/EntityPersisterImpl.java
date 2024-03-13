package persistence.entity.impl;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityPersister;
import persistence.sql.QueryBuilder;

public class EntityPersisterImpl implements EntityPersister {

    private static final Logger log = LoggerFactory.getLogger(EntityPersisterImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private final QueryBuilder queryBuilder;

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, new QueryBuilder());
    }

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate, QueryBuilder queryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public boolean update(Object entity) {
        try {
            String updateQuery = queryBuilder.getUpdateQuery(entity);

            jdbcTemplate.execute(updateQuery);

            log.info("Entity updated successfully. SQL: {}", updateQuery);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void insert(Object entity) {
        String insertQuery = queryBuilder.getInsertQuery(entity);

        log.info("Entity inserted successfully. SQL: {}", insertQuery);

        jdbcTemplate.execute(insertQuery);
    }

    @Override
    public void delete(Object entity) {
        String deleteQueryFromEntity = queryBuilder.getDeleteQueryFromEntity(entity);

        log.info("Entity deleteted successfully. SQL: {}", deleteQueryFromEntity);

        jdbcTemplate.execute(deleteQueryFromEntity);
    }
}
