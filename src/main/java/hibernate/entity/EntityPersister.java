package hibernate.entity;

import hibernate.dml.DeleteQueryBuilder;
import hibernate.dml.InsertQueryBuilder;
import jdbc.JdbcTemplate;

public class EntityPersister<T> {

    private final EntityClass<T> entityClass;
    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    public EntityPersister(final Class<T> clazz, final JdbcTemplate jdbcTemplate) {
        this.entityClass = new EntityClass<>(clazz);
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(final Object entity) {
        final String query = insertQueryBuilder.generateQuery(entityClass, entity);
        jdbcTemplate.execute(query);
    }

    public void delete(final Object entity) {
        final String query = deleteQueryBuilder.generateQuery(entityClass, entity);
        jdbcTemplate.execute(query);
    }
}
