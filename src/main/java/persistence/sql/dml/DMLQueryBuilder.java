package persistence.sql.dml;

import persistence.entity.EntityValue;
import persistence.entity.metadata.EntityColumn;
import persistence.entity.metadata.DefaultEntityMetadataReader;
import persistence.entity.metadata.EntityMetadataReader;

import java.util.List;
import java.util.stream.Collectors;

public class DMLQueryBuilder {

    private static DMLQueryBuilder dmlQueryBuilder = null;
    private EntityMetadataReader entityMetadataReader;
    private EntityValue entityValue;

    private DMLQueryBuilder() {
        entityMetadataReader = new DefaultEntityMetadataReader();
        entityValue = new EntityValue(entityMetadataReader);
    }

    public static DMLQueryBuilder getInstance() {
        if (dmlQueryBuilder == null) {
            dmlQueryBuilder = new DMLQueryBuilder();
        }
        return dmlQueryBuilder;
    }

    private static final  String COLUMN_SEPARATOR = ", ";

    public String insertSql(Object entity) {
        String tableName = entityMetadataReader.getTableName(entity.getClass());
        String columns = getColumnNamesClause(entityMetadataReader.getInsertTargetColumns(entity.getClass()));
        String columnValues = getColumnValueClause(entity, entityMetadataReader.getInsertTargetColumns(entity.getClass()));

        return DMLQueryFormatter.createInsertQuery(tableName, columns, columnValues);
    }

    private String getColumnValueClause(Object entity, List<EntityColumn> insertTargetColumns) {
        return insertTargetColumns.stream()
            .map(column -> getColumnValueWithSqlFormat(entity, column.getColumnName()))
            .collect(Collectors.joining(COLUMN_SEPARATOR));
    }

    public String selectAllSql(Class<?> clazz) {
        String columnsClause = entityMetadataReader.getColumns(clazz).stream()
            .map(EntityColumn::getColumnName)
            .collect(Collectors.joining(COLUMN_SEPARATOR));

        return DMLQueryFormatter.createSelectQuery(columnsClause, entityMetadataReader.getTableName(clazz));
    }

    public String selectByIdQuery(Class<?> clazz, Object id) {
        String sql = selectAllSql(clazz);
        String condition = createCondition(entityMetadataReader.getIdColumnName(clazz), id, "=");

        return DMLQueryFormatter.createSelectByConditionQuery(sql, condition);
    }

    public String deleteSql(Object entity) {
        String tableName = entityMetadataReader.getTableName(entity.getClass());
        String deleteConditionClause = wherePrimaryKeyClause( entity);

        return DMLQueryFormatter.createDeleteQuery(tableName, deleteConditionClause);
    }

    public String updateSql(Object entity) {
        String tableName = entityMetadataReader.getTableName(entity.getClass());
        String columnValueSetClause = columnValueSetClause(entity, entityMetadataReader.getInsertTargetColumns(entity.getClass()));
        String conditionClause = wherePrimaryKeyClause(entity);

        return DMLQueryFormatter.createUpdateQuery(tableName, columnValueSetClause, conditionClause);
    }

    private String columnValueSetClause(Object entity, List<EntityColumn> insertTargetColumns) {
        return insertTargetColumns.stream()
            .map(column -> column.getColumnName() + " = " + getColumnValueWithSqlFormat(entity, column.getColumnName()))
            .collect(Collectors.joining(COLUMN_SEPARATOR));
    }

    private String wherePrimaryKeyClause(Object object) {
        String idColumnName = entityMetadataReader.getIdColumnName(object.getClass());
        Long value = getColumnValue(object, idColumnName);

        return createCondition(idColumnName, value, "=");
    }

    private String createCondition(String columnName, Object value, String operator) {

        return String.format("%s %s %s", columnName, operator, formatValue(value));
    }

    private String getColumnNamesClause(List<EntityColumn> insertTargetColumns) {
        return insertTargetColumns.stream()
            .map(EntityColumn::getColumnName)
            .collect(Collectors.joining(COLUMN_SEPARATOR));
    }

    private String getColumnValueWithSqlFormat(Object entity, String columnName) {
        return formatValue(getColumnValue(entity, columnName));
    }

    private <T> T getColumnValue(Object entity, String columnName) {
        return entityValue.getValue(entity, columnName);
    }

    private String formatValue(Object value) {
        if (value instanceof String) {

            return "'" + value + "'";
        }

        return value == null ? "" : value.toString();
    }

}
