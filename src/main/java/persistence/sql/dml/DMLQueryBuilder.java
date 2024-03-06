package persistence.sql.dml;

import persistence.entity.metadata.EntityColumn;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DMLQueryBuilder {

    private static DMLQueryBuilder dmlQueryBuilder = null;

    public static DMLQueryBuilder getInstance() {
        if (dmlQueryBuilder == null) {
            dmlQueryBuilder = new DMLQueryBuilder();
        }
        return dmlQueryBuilder;
    }

    private static final  String COLUMN_SEPARATOR = ", ";

    public String insertSql(String tableName, List<EntityColumn> columns, Map<String, Object> columnValues) {
        return DMLQueryFormatter.createInsertQuery(tableName, getColumnNamesClause(columns), columnValueSetClause(columns, columnValues))   ;
    }

    private String getColumnValueClause(Object entity, List<EntityColumn> insertTargetColumns) {
        return insertTargetColumns.stream()
            .map(column -> getColumnValueWithSqlFormat(entity, column.getColumnName()))
            .collect(Collectors.joining(COLUMN_SEPARATOR));
    }

    public String selectAllSql(Class<?> clazz) {
        String columnsClause = DefaultEntityMetadataReader.getColumns(clazz).stream()
            .map(EntityColumn::getColumnName)
            .collect(Collectors.joining(COLUMN_SEPARATOR));

        return DMLQueryFormatter.createSelectQuery(columnsClause, DefaultEntityMetadataReader.getTableName(clazz));
    }

    public String selectByIdQuery(Class<?> clazz, Object id) {
        String sql = selectAllSql(clazz);
        String condition = createCondition(DefaultEntityMetadataReader.getIdColumnName(clazz), id, "=");

        return DMLQueryFormatter.createSelectByConditionQuery(sql, condition);
    }

    public String deleteSql(Object entity) {
        String tableName = DefaultEntityMetadataReader.getTableName(entity.getClass());
        String deleteConditionClause = wherePrimaryKeyClause( entity);

        return DMLQueryFormatter.createDeleteQuery(tableName, deleteConditionClause);
    }

    public String updateSql(Object entity) {
        String tableName = DefaultEntityMetadataReader.getTableName(entity.getClass());
        String columnValueSetClause = columnValueSetClause(entity, DefaultEntityMetadataReader.getInsertTargetColumns(entity.getClass()));
        String conditionClause = wherePrimaryKeyClause(entity);

        return DMLQueryFormatter.createUpdateQuery(tableName, columnValueSetClause, conditionClause);
    }

    private String columnValueSetClause(Object entity, List<EntityColumn> insertTargetColumns) {
        return insertTargetColumns.stream()
            .map(column -> column.getColumnName() + " = " + getColumnValueWithSqlFormat(entity, column.getColumnName()))
            .collect(Collectors.joining(COLUMN_SEPARATOR));
    }

    private String columnValueSetClause(List<EntityColumn> columns, Map<String, Object> columnValues) {
        return columns.stream()
            .map(column -> column.getColumnName() + " = " + formatValue(columnValues.get(column.getColumnName())))
            .collect(Collectors.joining(COLUMN_SEPARATOR));
    }


    private String wherePrimaryKeyClause(Object object) {
        String idColumnName = DefaultEntityMetadataReader.getIdColumnName(object.getClass());
        Long value = (Long) getColumnValue(object, idColumnName);

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

    private Object getColumnValue(Object entity, String columnName) {
        return EntityDataManipulator.getValue(entity, columnName);
    }

    private String formatValue(Object value) {
        if (value instanceof String) {

            return "'" + value + "'";
        }

        return value == null ? "" : value.toString();
    }


}
