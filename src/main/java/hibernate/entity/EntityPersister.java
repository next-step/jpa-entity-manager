package hibernate.entity;

import hibernate.dml.DeleteQueryBuilder;
import hibernate.dml.InsertQueryBuilder;
import hibernate.dml.UpdateQueryBuilder;
import hibernate.entity.meta.EntityClass;
import hibernate.entity.meta.column.EntityColumn;
import jdbc.JdbcTemplate;

import java.util.Map;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.INSTANCE;
    private final DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;
    private final UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.INSTANCE;

    public EntityPersister(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(final EntityClass<?> entityClass, final Object entityId, final Map<EntityColumn, Object> updateFields) {
        final String query = updateQueryBuilder.generateQuery(
                entityClass.tableName(),
                updateFields,
                entityClass.getEntityId(),
                entityId
        );
        return jdbcTemplate.executeUpdate(query);
    }

    public Object insert(final Object entity) {
        EntityClass<?> entityClass = EntityClass.getInstance(entity.getClass());
        final String query = insertQueryBuilder.generateQuery(
                entityClass.tableName(),
                entityClass.getFieldValues(entity)
        );
        return jdbcTemplate.executeInsert(query);
    }

    public void delete(final Object entity) {
        EntityClass<?> entityClass = EntityClass.getInstance(entity.getClass());
        EntityColumn entityId = entityClass.getEntityId();
        final String query = deleteQueryBuilder.generateQuery(
                entityClass.tableName(),
                entityId,
                entityId.getFieldValue(entity)
        );
        jdbcTemplate.execute(query);
    }
}
