package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class EntityManagerImpl<T> implements EntityManager<T> {
    private final EntityPersist entityPersist;
    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersist = new EntityPersist(jdbcTemplate);
        this.selectQueryBuilder = new SelectQueryBuilder();
        this.insertQueryBuilder = new InsertQueryBuilder();
    }

    @Override
    public T find(Class<T> clazz, Long id) {
        return jdbcTemplate.queryForObject(selectQueryBuilder.findById(clazz, id), new RowMapperImpl<>(clazz));
    }

    @Override
    public void persist(T entity) {
        jdbcTemplate.execute(insertQueryBuilder.generateSQL(entity));
    }

    @Override
    public void remove(T entity) {
        entityPersist.delete(entity);
    }
}
