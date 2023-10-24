package hibernate.entity;

import hibernate.dml.DeleteQueryBuilder;
import hibernate.dml.InsertQueryBuilder;
import hibernate.dml.UpdateQueryBuilder;
import hibernate.entity.column.EntityColumn;
import hibernate.entity.column.EntityColumns;
import jdbc.JdbcTemplate;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityPersister<T> {

    private final EntityTableName tableName;
    private final EntityColumns entityColumns;
    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.INSTANCE;
    private final DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;
    private final UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.INSTANCE;

    public EntityPersister(final Class<T> clazz, final JdbcTemplate jdbcTemplate) {
        this.tableName = new EntityTableName(clazz);
        this.entityColumns = new EntityColumns(clazz.getDeclaredFields());
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(final Object entity) {
        EntityColumn entityId = entityColumns.getEntityId();
        final String query = updateQueryBuilder.generateQuery(
                tableName.getTableName(),
                getFieldValues(entity),
                entityId,
                entityId.getFieldValue(entity)
        );
        return jdbcTemplate.executeUpdate(query);
    }

    public void insert(final Object entity) {
        final String query = insertQueryBuilder.generateQuery(
                tableName.getTableName(),
                getFieldValues(entity)
        );
        jdbcTemplate.execute(query);
    }

    public void delete(final Object entity) {
        EntityColumn entityId = entityColumns.getEntityId();
        final String query = deleteQueryBuilder.generateQuery(
                tableName.getTableName(),
                entityId,
                entityId.getFieldValue(entity)
        );
        jdbcTemplate.execute(query);
    }

    private Map<EntityColumn, Object> getFieldValues(final Object entity) {
        return entityColumns.getValues()
                .stream()
                .map(entityColumn -> new AbstractMap.SimpleEntry<>(entityColumn, entityColumn.getFieldValue(entity)))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }
}
