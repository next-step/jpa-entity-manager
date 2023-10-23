package hibernate.entity;

import hibernate.dml.InsertQueryBuilder;
import jdbc.JdbcTemplate;

public class EntityPersister<T> {

    private final EntityClass<T> entityClass;
    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();

    public EntityPersister(final Class<T> clazz, final JdbcTemplate jdbcTemplate) {
        this.entityClass = new EntityClass<>(clazz);
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(final Object entity) {
        final String query = insertQueryBuilder.generateQuery(entityClass, entity);
        jdbcTemplate.execute(query);
    }
}
