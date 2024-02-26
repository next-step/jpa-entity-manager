package database.sql.dml;

import database.sql.util.EntityMetadata;

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

    public DeleteQueryBuilder(Class<?> entityClass) {
        this(new EntityMetadata(entityClass));
    }

    public String buildQuery(Map<String, Object> conditionMap) {
        return String.format("DELETE FROM %s WHERE %s", tableName, whereClause(conditionMap));
    }

    public String buildQuery(long id) {
        return buildQuery(Map.of(primaryKeyColumnName, id));
    }

    private String whereClause(Map<String, Object> conditionMap) {
        return new WhereClause(conditionMap, allColumnNames).toQuery();
    }
}
