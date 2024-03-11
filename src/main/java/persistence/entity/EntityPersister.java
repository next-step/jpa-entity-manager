package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.ddl.PrimaryKeyClause;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(Object entity, Long id) {
        String query = new UpdateQueryBuilder(entity.getClass()).getQuery(entity, id);
        int result = jdbcTemplate.executeUpdate(query);
        return result == 1;
    }

    public void insert(Object entity) {
        Class<?> clazz = entity.getClass();
        String queryToInsert = new InsertQueryBuilder(clazz).getInsertQuery(entity);
        jdbcTemplate.execute(queryToInsert);
    }

    public Long delete(Object entity) {
        Long id = PrimaryKeyClause.primaryKeyValue(entity);
        String query = new DeleteQueryBuilder(entity.getClass()).deleteById(id);
        jdbcTemplate.execute(query);
        return id;
    }
}
