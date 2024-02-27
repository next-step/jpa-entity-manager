package persistence.entity;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.column.IdColumn;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class EntityPersisterImpl implements EntityPersister {

    private static Logger log = LoggerFactory.getLogger(EntityPersisterImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
        this.updateQueryBuilder = new UpdateQueryBuilder(dialect);
        this.insertQueryBuilder = new InsertQueryBuilder(dialect);
        this.deleteQueryBuilder = new DeleteQueryBuilder(dialect);
    }

    @Override
    public boolean update(Object entity, IdColumn idColumn) {

        UpdateQueryBuilder queryBuilder = updateQueryBuilder.build(entity);
        String query = queryBuilder.updateById(idColumn.getValue());
        try {
            jdbcTemplate.execute(query);
            return true;
        } catch (Exception e) {
            log.info("Error updating entity: {} and id: {}", entity.getClass().getSimpleName(), idColumn.getValue());
            return false;
        }
    }

    @Override
    public void insert(Object entity) {
        String query = insertQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }

    @Override
    public void delete(Object entity, IdColumn idColumn) {
        DeleteQueryBuilder queryBuilder = deleteQueryBuilder.build(entity);
        String query = queryBuilder.deleteById(idColumn.getValue());
        jdbcTemplate.execute(query);
    }
}
