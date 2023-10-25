package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.core.*;
import persistence.exception.PersistenceException;
import persistence.sql.dml.DmlGenerator;
import persistence.util.ReflectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EntityPersister {
    private final PersistenceEnvironment persistenceEnvironment;
    private final String tableName;
    private final EntityColumn idColumn;
    private final EntityColumns columns;
    private final EntityColumns insertableColumns;
    private final DmlGenerator dmlGenerator;

    public EntityPersister(final Class<?> clazz, final PersistenceEnvironment persistenceEnvironment) {
        final EntityMetadata<?> entityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(clazz);
        this.persistenceEnvironment = persistenceEnvironment;
        this.tableName = entityMetadata.getTableName();
        this.idColumn = entityMetadata.getIdColumn();
        this.columns = entityMetadata.getColumns();
        this.insertableColumns = entityMetadata.getInsertableColumn();
        this.dmlGenerator = persistenceEnvironment.getDmlGenerator();
    }

    public void insert(final Object entity) {
        final String insertQuery = renderInsert(entity);
        executeQuery(insertQuery);
    }

    public void update(final Object entity) {
        final String updateQuery = renderUpdate(entity);
        executeQuery(updateQuery);
    }

    public void delete(final Object entity) {
        final String deleteQuery = renderDelete(entity);
        executeQuery(deleteQuery);
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

    private void executeQuery(final String query) {
        try (final Connection connection = persistenceEnvironment.getConnection()) {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
            jdbcTemplate.execute(query);
        } catch (final SQLException e) {
            throw new PersistenceException("SQL 실행 중 오류가 발생했습니다.", e);
        }
    }
}
