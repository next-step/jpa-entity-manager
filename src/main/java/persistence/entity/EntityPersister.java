package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

public class EntityPersister {

    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;

    private EntityPersister(JdbcTemplate jdbcTemplate) {
        this.dmlGenerator = DmlGenerator.getInstance();
        this.jdbcTemplate = jdbcTemplate;
    }

    public static EntityPersister from(JdbcTemplate jdbcTemplate) {
        return new EntityPersister(jdbcTemplate);
    }

    public boolean update(Object entity) {
        return jdbcTemplate.executeUpdate(dmlGenerator.generateUpdateQuery(entity)) > 0;
    }

    public void insert(Object entity) {
        jdbcTemplate.execute(dmlGenerator.generateInsertQuery(entity));
    }

    public void delete(Object entity) {
        jdbcTemplate.execute(dmlGenerator.generateDeleteQuery(entity));
    }
}
