package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.core.EntityColumn;
import persistence.core.EntityColumns;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.sql.dml.DmlGenerator;
import persistence.util.ReflectionUtils;

import java.util.List;

public class EntityPersister {
    private final String tableName;
    private final EntityColumn idColumn;
    private final EntityColumns columns;
    private final EntityColumns insertableColumns;
    private final JdbcTemplate jdbcTemplate;
    private final DmlGenerator dmlGenerator;

    public EntityPersister(final Class<?> clazz, final JdbcTemplate jdbcTemplate, final DmlGenerator dmlGenerator) {
        final EntityMetadata<?> entityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(clazz);
        this.tableName = entityMetadata.getTableName();
        this.idColumn = entityMetadata.getIdColumn();
        this.columns = entityMetadata.getColumns();
        this.insertableColumns = entityMetadata.getInsertableColumn();
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

    public String renderInsert(final Object entity) {
        final List<String> columnNames = insertableColumns.getNames();
        final List<Object> values = ReflectionUtils.getFieldValues(entity, insertableColumns.getFieldNames());
        return dmlGenerator.insert(tableName, columnNames, values);
    }

    public String renderUpdate(final Object entity) {
        final List<String> columnNames = insertableColumns.getNames();
        final List<Object> values = ReflectionUtils.getFieldValues(entity, insertableColumns.getFieldNames());
        final Object idValue = ReflectionUtils.getFieldValue(entity, idColumn.getFieldName());
        return dmlGenerator.update(tableName, columnNames, values, idColumn.getName(), idValue);
    }

    public String renderDelete(final Object entity) {
        final Object idValue = ReflectionUtils.getFieldValue(entity, idColumn.getFieldName());
        return dmlGenerator.delete(tableName, idColumn.getName(), idValue);
    }

    public List<String> getColumnNames() {
        return columns.getNames();
    }

    public List<String> getColumnFieldNames() {
        return columns.getFieldNames();
    }

    public int getColumnSize() {
        return columns.size();
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumnName() {
        return idColumn.getName();
    }
}