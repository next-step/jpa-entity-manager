package database.sql.dml;

import database.sql.util.EntityMetadata;

import java.util.List;
import java.util.Map;

public class SelectQueryBuilder {
    private final String tableName;
    private final List<String> allColumnNames;
    private final String joinedAllColumnNames;

    public SelectQueryBuilder(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);
        this.tableName = entityMetadata.getTableName();
        this.allColumnNames = entityMetadata.getAllColumnNames();
        this.joinedAllColumnNames = entityMetadata.getJoinedAllColumnNames();
    }

    public String buildQuery(Map<String, Object> conditionMap) {
        return String.format("SELECT %s FROM %s WHERE %s", joinedAllColumnNames, tableName, whereClause(conditionMap));
    }

    public String buildQuery() {
        return buildQuery(Map.of());
    }

    private String whereClause(Map<String, Object> conditionMap) {
        return new WhereClause(conditionMap, allColumnNames).toQuery();
    }
}
