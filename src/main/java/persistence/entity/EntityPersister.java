package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.common.DtoMapper;
import persistence.sql.ddl.PrimaryKeyClause;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Object update(Object entity, Long id) {
        String query = new UpdateQueryBuilder(entity.getClass()).getQuery(entity, id);
        int result = jdbcTemplate.executeUpdate(query);
        return findInsertedRow(entity.getClass());
    }

    public Object insert(Object entity) {
        Class<?> clazz = entity.getClass();
        String queryToInsert = new InsertQueryBuilder(clazz).getInsertQuery(entity);
        jdbcTemplate.execute(queryToInsert);

        return findInsertedRow(clazz);
    }

    private Object findInsertedRow(Class<?> clazz) {
        String queryToFindAll = new SelectQueryBuilder(clazz).getFindAllQuery();

        return jdbcTemplate.query(queryToFindAll, new DtoMapper<>(clazz))
                .stream()
                .reduce((first, second) -> second).get();
    }

    public void delete(Object entity) {
        Long id = PrimaryKeyClause.primaryKeyValue(entity);
        String query = new DeleteQueryBuilder(entity.getClass()).deleteById(id);
        jdbcTemplate.execute(query);
    }
}
