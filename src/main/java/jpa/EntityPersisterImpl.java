package jpa;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.SelectQuery;
import persistence.sql.dml.UpdateQuery;
import persistence.sql.entity.EntityRowMapper;
import persistence.sql.model.EntityId;
import persistence.sql.model.EntityIdInjector;

import java.util.Objects;

public class EntityPersisterImpl implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void update(Object entity) {
        UpdateQuery updateQuery = UpdateQuery.getInstance();
        jdbcTemplate.execute(updateQuery.makeQuery(entity));
    }

    @Override
    public <T> T insert(T entity) {
        InsertQuery insertQuery = InsertQuery.getInstance();
        return EntityIdInjector.getInstance().inject(
                entity,
                jdbcTemplate.insertAndGetId(insertQuery.makeQuery(entity))
        );
    }

    @Override
    public void delete(Object entity) {
        DeleteQuery deleteQuery = DeleteQuery.getInstance();
        jdbcTemplate.execute(deleteQuery.makeQuery(entity));
    }
}
