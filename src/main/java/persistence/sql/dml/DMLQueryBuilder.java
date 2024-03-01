package persistence.sql.dml;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import persistence.core.EntityContextManager;
import persistence.entity.metadata.EntityColumn;
import persistence.entity.metadata.EntityMetadata;
import persistence.inspector.EntityInfoExtractor;

public class DMLQueryBuilder extends EntityContextManager {

    private DMLQueryBuilder() {
    }

    public static DMLQueryBuilder getInstance() {
        return new DMLQueryBuilder();
    }

    private final static String COLUMN_SEPARATOR = ", ";

    public String insertSql(Object entity) {
        EntityMetadata entityMetadata = getEntityMetadata(entity.getClass());
        String tableName = entityMetadata.getTableName();
        String columns = getColumnNamesClause(entityMetadata.getInsertTargetColumns());
        String columnValues = getColumnValueClause(entity, entityMetadata.getInsertTargetColumns());

        return DMLQueryFormatter.createInsertQuery(tableName, columns, columnValues);
    }

    private String getColumnValueClause(Object entity, List<EntityColumn> insertTargetColumns) {
        return insertTargetColumns.stream()
            .map(column -> getColumnValueWithSqlFormat(entity, column.getName()))
            .collect(Collectors.joining(COLUMN_SEPARATOR));
    }

    public String selectAllSql(Class<?> clazz) {
        EntityMetadata entityMetadata = getEntityMetadata(clazz);
        String columnsClause = entityMetadata.getColumns().stream()
            .map(EntityColumn::getName)
            .collect(Collectors.joining(COLUMN_SEPARATOR));

        return DMLQueryFormatter.createSelectQuery(columnsClause, entityMetadata.getTableName());
    }

    public String selectByIdQuery(Class<?> clazz, Object id) {
        EntityMetadata entityMetadata = getEntityMetadata(clazz);
        String sql = selectAllSql(clazz);
        String condition = createCondition(entityMetadata.getIdColumn().getName(), id, "=");

        return DMLQueryFormatter.createSelectByConditionQuery(sql, condition);
    }

    public String deleteSql(Object entity) {
        EntityMetadata entityMetadata = getEntityMetadata(entity.getClass());
        String tableName = entityMetadata.getTableName();
        String deleteConditionClause = wherePrimaryKeyClause(entityMetadata, entity);

        return DMLQueryFormatter.createDeleteQuery(tableName, deleteConditionClause);
    }

    private String wherePrimaryKeyClause(EntityMetadata entityMetadata, Object object) {
        String idColumnName = entityMetadata.getIdColumn().getName();
        Object value = getColumnValue(object, entityMetadata.getIdColumn().getName());

        return createCondition(idColumnName, value, "=");
    }

    private String createCondition(String columnName, Object value, String operator) {

        return String.format("%s %s %s", columnName, operator, formatValue(value));
    }

    private String getColumnNamesClause(List<EntityColumn> insertTargetColumns) {
        return insertTargetColumns.stream()
            .map(EntityColumn::getName)
            .collect(Collectors.joining(COLUMN_SEPARATOR));
    }

    private String getColumnValueWithSqlFormat(Object entity, String columnName) {
        return formatValue(getColumnValue(entity, columnName));
    }

    private Object getColumnValue(Object entity, String columnName) {
        Field field = EntityInfoExtractor.getFieldByColumnName(entity.getClass(), columnName);

        return EntityInfoExtractor.getFieldValue(entity, field);
    }

    private String formatValue(Object value) {
        if (value instanceof String) {

            return "'" + value + "'";
        }

        return value == null ? "" : value.toString();
    }

}
