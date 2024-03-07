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

    public boolean update(Object entity) {
        int result = jdbcTemplate.executeUpdate(new UpdateQueryBuilder(entity.getClass()).getQuery(entity));
        return result == 1;
    }

    //TODO: (질문할것) insert에서 객체를 반환하는 방향으로 설계를 했는데요. 미션 페이지에서는 반환 타입이 void 더라구요. 반환하지 않는것이 이번 미션의 방향일까요?
    public Object insert(Object entity) {
        Class<?> clazz = entity.getClass();
        String queryToInsert = new InsertQueryBuilder(clazz).getInsertQuery(entity);
        jdbcTemplate.execute(queryToInsert);

        String queryToSelect = new SelectQueryBuilder(clazz).getFindLastRowQuery();
        return jdbcTemplate.queryForObject(queryToSelect, new DtoMapper<>(clazz));
    }

    public void delete(Object entity) {
        Long id = PrimaryKeyClause.primaryKeyValue(entity);
        String query = new DeleteQueryBuilder(entity.getClass()).deleteById(id);
        jdbcTemplate.execute(query);
    }
}
