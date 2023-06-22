package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class BasicEntityManger implements EntityManager {
    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;
    private final InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.INSTANCE;
    private final DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;

    public BasicEntityManger(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        String selectQuery = selectQueryBuilder.findById(clazz, id);
        return jdbcTemplate.queryForObject(selectQuery, new RowMapperImpl<>(clazz));
    }

    @Override
    public void persist(Object entity) {
        String insertQuery = insertQueryBuilder.insert(entity);
        jdbcTemplate.execute(insertQuery);
    }

    @Override
    public void remove(Object entity) {
        String deleteQuery = deleteQueryBuilder.delete(entity);
        jdbcTemplate.execute(deleteQuery);
    }
}
