package hibernate.entity;

import hibernate.dml.DeleteQueryBuilder;
import hibernate.dml.InsertQueryBuilder;
import hibernate.dml.UpdateQueryBuilder;
import hibernate.entity.column.EntityColumn;
import hibernate.entity.column.EntityColumns;
import jdbc.JdbcTemplate;

public class EntityPersister<T> {

    private final Class<T> clazz;
    private final EntityTableName tableName;
    private final EntityColumns entityColumns;
    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.INSTANCE;
    private final DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;
    private final UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.INSTANCE;

    public EntityPersister(final Class<T> clazz, final JdbcTemplate jdbcTemplate) {
        this.clazz = clazz;
        this.tableName = new EntityTableName(clazz);
        this.entityColumns = new EntityColumns(clazz.getDeclaredFields());
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(final Object entity) {
        validateEntityType(entity);
        EntityColumn entityId = entityColumns.getEntityId();
        final String query = updateQueryBuilder.generateQuery(
                tableName.getTableName(),
                entityColumns.getFieldValues(entity),
                entityId,
                entityId.getFieldValue(entity)
        );
        return jdbcTemplate.executeUpdate(query);
    }

    public void insert(final Object entity) {
        validateEntityType(entity);
        final String query = insertQueryBuilder.generateQuery(
                tableName.getTableName(),
                entityColumns.getFieldValues(entity)
        );
        jdbcTemplate.execute(query);
    }

    public void delete(final Object entity) {
        validateEntityType(entity);
        EntityColumn entityId = entityColumns.getEntityId();
        final String query = deleteQueryBuilder.generateQuery(
                tableName.getTableName(),
                entityId,
                entityId.getFieldValue(entity)
        );
        jdbcTemplate.execute(query);
    }

    private void validateEntityType(final Object entity) {
        if (clazz != entity.getClass()) {
            throw new IllegalArgumentException("EntityClass와 일치하지 않는 객체입니다.");
        }
    }
}
