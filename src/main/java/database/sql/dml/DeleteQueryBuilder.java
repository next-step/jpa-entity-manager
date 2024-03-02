package database.sql.dml;

import database.mapping.EntityMetadata;

import java.util.List;
import java.util.Map;

public class DeleteQueryBuilder {
    private final String tableName;
    private final List<String> allColumnNames;
    private final String primaryKeyColumnName;

    public DeleteQueryBuilder(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
        this.primaryKeyColumnName = entityMetadata.getPrimaryKeyColumnName();
        this.allColumnNames = entityMetadata.getAllColumnNames();
    }

    public DeleteQueryBuilder(Class<?> clazz) {
        this(EntityMetadata.fromClass(clazz));
    }

    public String buildQuery(Map<String, Object> conditionMap) {
        return String.format("DELETE FROM %s WHERE %s", tableName, whereClause(conditionMap));
    }

    public String buildQuery(long id) {
        return buildQuery(Map.of(primaryKeyColumnName, id));
    }

    private String whereClause(Map<String, Object> conditionMap) {
        return WhereClause.from(conditionMap, allColumnNames).toQuery();
    }
}
