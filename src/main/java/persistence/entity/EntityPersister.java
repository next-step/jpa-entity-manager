package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.DeleteByIdQueryBuilder;
import persistence.sql.dml.query.InsertQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

public class EntityPersister {
    private final TableDefinition tableDefinition;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(Class<?> clazz,
                           JdbcTemplate jdbcTemplate) {
        this.tableDefinition = new TableDefinition(clazz);
        this.jdbcTemplate = jdbcTemplate;
    }

    public Object getEntityId(Object entity) {
        return tableDefinition.tableId().getValue(entity);
    }

    public void update(Object entity) {
        String query = new UpdateQueryBuilder(entity).build();
        jdbcTemplate.execute(query);
    }

    public void insert(Object entity) {
        String query = new InsertQueryBuilder(entity).build();
        jdbcTemplate.execute(query);
    }

    public void delete(Object entity) {
        String query = new DeleteByIdQueryBuilder(entity).build();
        jdbcTemplate.execute(query);
    }
}
