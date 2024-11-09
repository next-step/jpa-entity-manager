package persistence.entity.impl;

import jdbc.JdbcTemplate;
import java.util.Optional;

import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;


public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private final EntityLoader entityLoader;


    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityLoader = new EntityLoader<>(jdbcTemplate);
    }

    public <T> Optional<T> find(Class<T> clazz, Long id) {
        return Optional.ofNullable(clazz.cast(entityLoader.load(clazz, id)));
    }

    public void update(Object entity) {
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(entity.getClass());
        String updateQuery = updateQueryBuilder.update(entity);
        jdbcTemplate.execute(updateQuery);
    }

    public void remove(Class<?> clazz, Long id) {
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(clazz);
        String deleteQuery = deleteQueryBuilder.deleteById(clazz, id);
        jdbcTemplate.execute(deleteQuery);
    }

    public Long insert(Object entity) {
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(entity.getClass());
        String insertQuery = insertQueryBuilder.insert(entity);
        return jdbcTemplate.executeInsert(insertQuery);

    }
}
