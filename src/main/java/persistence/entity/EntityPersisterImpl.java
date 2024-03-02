package persistence.entity;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class EntityPersisterImpl implements EntityPersister {

    private static Logger log = LoggerFactory.getLogger(EntityPersisterImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.updateQueryBuilder = new UpdateQueryBuilder(dialect);
        this.insertQueryBuilder = new InsertQueryBuilder(dialect);
        this.deleteQueryBuilder = new DeleteQueryBuilder(dialect);
    }

    @Override
    public boolean update(Object entity, Object id) {

        UpdateQueryBuilder queryBuilder = updateQueryBuilder.build(entity);
        String query = queryBuilder.toStatementWithId(id);
        try {
            jdbcTemplate.execute(query);
            return true;
        } catch (Exception e) {
            log.info("Error updating entity: {} and id: {}", entity.getClass().getSimpleName(), id);
            return false;
        }
    }

    @Override
    public long insertByGeneratedKey(Object entity) {
        String query = insertQueryBuilder.build(entity);
        return jdbcTemplate.executeInsertByGeneratedKey(query);
    }

    @Override
    public void insert(Object entity) {
        String query = insertQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }

    @Override
    public void delete(Object entity, Object id) {
        DeleteQueryBuilder queryBuilder = deleteQueryBuilder.build(entity);
        String query = queryBuilder.toStatementWithId(id);
        jdbcTemplate.execute(query);
    }
}
