package hibernate.entity;

import hibernate.dml.DeleteQueryBuilder;
import hibernate.dml.InsertQueryBuilder;
import hibernate.dml.UpdateQueryBuilder;
import jdbc.JdbcTemplate;

public class EntityPersister<T> {

    private final EntityClass<T> entityClass;
    private final EntityTableName tableName;
    private final Class<T> clazz;
    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.INSTANCE;
    private final DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;
    private final UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.INSTANCE;

    public EntityPersister(final Class<T> clazz, final JdbcTemplate jdbcTemplate) {
        this.tableName = new EntityTableName(clazz);
        this.clazz = clazz;
        this.entityClass = new EntityClass<>(clazz);
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(final Object entity) {
        final String query = updateQueryBuilder.generateQuery(
                tableName.getTableName(),
                entityClass.getFieldValues(entity),
                entityClass.getEntityId(),
                entityClass.extractEntityId(entity)
        );
        return jdbcTemplate.executeUpdate(query);
    }

    public void insert(final Object entity) {
        final String query = insertQueryBuilder.generateQuery(
                tableName.getTableName(),
                entityClass.getFieldValues(entity)
        );
        jdbcTemplate.execute(query);
    }

    public void delete(final Object entity) {
        final String query = deleteQueryBuilder.generateQuery(
                tableName.getTableName(),
                entityClass.getEntityId(),
                entityClass.extractEntityId(entity)
        );
        jdbcTemplate.execute(query);
    }
}
