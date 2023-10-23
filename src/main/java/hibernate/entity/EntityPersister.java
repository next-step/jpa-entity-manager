package hibernate.entity;

import hibernate.dml.DeleteQueryBuilder;
import hibernate.dml.InsertQueryBuilder;
import hibernate.dml.UpdateQueryBuilder;
import jdbc.JdbcTemplate;

public class EntityPersister<T> {

    private final EntityClass<T> entityClass;
    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.INSTANCE;
    private final DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;
    private final UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.INSTANCE;

    public EntityPersister(final Class<T> clazz, final JdbcTemplate jdbcTemplate) {
        this.entityClass = new EntityClass<>(clazz);
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(final Object entity) {
        final String query = updateQueryBuilder.generateQuery(entityClass, entity);
        return jdbcTemplate.executeUpdate(query);
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
