package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.UpdateQueryBuilder;

public class EntityPersisterImpl implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean update(Object entity) {
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(entity);
        return jdbcTemplate.execute(updateQueryBuilder.build());
    }

    @Override
    public void insert(Object entity) {

    }

    @Override
    public void delete(Object entity) {

    }
}
