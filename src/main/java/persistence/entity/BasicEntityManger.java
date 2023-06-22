package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class BasicEntityManger implements EntityManager {
    private final JdbcTemplate jdbcTemplate;

    public BasicEntityManger(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz);
        String selectQuery = selectQueryBuilder.findById(id);
        return jdbcTemplate.queryForObject(selectQuery, new RowMapperImpl<>(clazz));
    }

    @Override
    public void persist(Object entity) {
        InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.INSTANCE;
        String insertQuery = insertQueryBuilder.insert(entity);
        jdbcTemplate.execute(insertQuery);
    }

    @Override
    public void remove(Object entity) {
        DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;
        String deleteQuery = deleteQueryBuilder.delete(entity);
        jdbcTemplate.execute(deleteQuery);
    }
}
