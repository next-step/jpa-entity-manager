package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.DeleteByIdQueryBuilder;
import persistence.sql.dml.query.InsertQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

public class EntityPersister {

    private static final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private static final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private static final DeleteByIdQueryBuilder deleteByIdQueryBuilder = new DeleteByIdQueryBuilder();

    private final TableDefinition tableDefinition;
    private final JdbcTemplate jdbcTemplate;


    public EntityPersister(Class<?> clazz,
                           JdbcTemplate jdbcTemplate) {
        this.tableDefinition = new TableDefinition(clazz);
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getEntityName() {
        return tableDefinition.entityName();
    }

    public Object getEntityId(Object entity) {
        return tableDefinition.tableId().getValue(entity);
    }

    public boolean update(Object entity) {
        String query = updateQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
        return true;
    }

    public void insert(Object entity) {
        String query = insertQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }

    public void delete(Object entity) {
        String query = deleteByIdQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }
}
