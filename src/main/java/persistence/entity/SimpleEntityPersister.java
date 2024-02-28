package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

public class SimpleEntityPersister implements EntityPersister {

    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;

    private SimpleEntityPersister(JdbcTemplate jdbcTemplate) {
        this.dmlGenerator = DmlGenerator.getInstance();
        this.jdbcTemplate = jdbcTemplate;
    }

    public static SimpleEntityPersister from(JdbcTemplate jdbcTemplate) {
        return new SimpleEntityPersister(jdbcTemplate);
    }

    @Override
    public boolean update(Object entity) {
        return jdbcTemplate.executeUpdate(dmlGenerator.generateUpdateQuery(entity)) > 0;
    }

    @Override
    public void insert(Object entity) {
        jdbcTemplate.execute(dmlGenerator.generateInsertQuery(entity));
    }

    @Override
    public void delete(Object entity) {
        jdbcTemplate.execute(dmlGenerator.generateDeleteQuery(entity));
    }
}
