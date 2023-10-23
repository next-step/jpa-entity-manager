package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.sql.dml.DmlGenerator;
import persistence.util.ReflectionUtils;

import java.util.List;

public class EntityPersister {
    private final EntityMetadata<?> entityMetadata;
    private final JdbcTemplate jdbcTemplate;
    private final DmlGenerator dmlGenerator;

    public EntityPersister(final Class<?> clazz, final JdbcTemplate jdbcTemplate, final DmlGenerator dmlGenerator) {
        this.entityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(clazz);
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = dmlGenerator;
    }

    public void insert(final Object entity) {
        final String insertQuery = renderInsert(entity);
        jdbcTemplate.execute(insertQuery);
    }

    public void update(final Object entity) {
        final String updateQuery = renderUpdate(entity);
        jdbcTemplate.execute(updateQuery);
    }

    public void delete(final Object entity) {
        final String deleteQuery = renderDelete(entity);
        jdbcTemplate.execute(deleteQuery);
    }

    public String renderSelect(final Long id) {
        return dmlGenerator.findById(entityMetadata.getTableName(), entityMetadata.getColumnNames(), entityMetadata.getIdColumnName(), id);
    }

    public String renderInsert(final Object entity) {
        final List<String> columnNames = entityMetadata.getInsertableColumnNames();
        final List<Object> values = ReflectionUtils.getFieldValues(entity, entityMetadata.getInsertableColumnFieldNames());
        return dmlGenerator.insert(entityMetadata.getTableName(), columnNames, values);
    }

    public String renderUpdate(final Object entity) {
        final List<String> columnNames = entityMetadata.getInsertableColumnNames();
        final List<Object> values = ReflectionUtils.getFieldValues(entity, entityMetadata.getInsertableColumnFieldNames());
        final Object idValue = ReflectionUtils.getFieldValue(entity, entityMetadata.getIdColumnFieldName());
        return dmlGenerator.update(entityMetadata.getTableName(), columnNames, values, entityMetadata.getIdColumnName(), idValue);
    }

    public String renderDelete(final Object entity) {
        final Object idValue = ReflectionUtils.getFieldValue(entity, entityMetadata.getIdColumnFieldName());
        return dmlGenerator.delete(entityMetadata.getTableName(), entityMetadata.getIdColumnName(), idValue);
    }

}
