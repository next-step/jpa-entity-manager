package persistence.sql.dml;

import persistence.sql.entity.EntityColumn;
import persistence.sql.entity.EntityData;
import util.ReflectionUtil;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    public String generateQuery(EntityData entityData, Object entity) {
        return "update " + entityData.getTableName()
                + appendSetClause(entityData, entity)
                + appendWhereClauseWithId(entityData.getEntityColumns().getIdColumn(), entity);
    }

    private String appendSetClause(EntityData entityData, Object entity) {
        return " set " + setColumnAndField(entityData.getEntityColumns().getEntityColumnList(), entity);
    }

    private String setColumnAndField(List<EntityColumn> entityColumnList, Object entity) {
        return entityColumnList
                .stream()
                .filter(entityColumn -> !entityColumn.isId())
                .map(entityColumn -> getEachColumnAndValue(entityColumn, entity))
                .collect(Collectors.joining(", "));
    }

    private String getEachColumnAndValue(EntityColumn entityColumn, Object entity) {
        return entityColumn.getColumnName() + " = " + valueToString(ReflectionUtil.getValueFrom(entityColumn.getField(), entity));
    }

    public String appendWhereClauseWithId(EntityColumn entityColumn, Object entity) {
        return " where "
                + entityColumn.getColumnName()
                + " = "
                + valueToString(ReflectionUtil.getValueFrom(entityColumn.getField(), entity));
    }

    private String valueToString(Object value) {
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return String.valueOf(value);
    }

}
