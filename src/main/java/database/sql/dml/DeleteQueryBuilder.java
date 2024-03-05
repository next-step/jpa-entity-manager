package database.sql.dml;

import database.mapping.EntityMetadata;
import database.sql.dml.where.WhereClause;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

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
        StringJoiner query = new StringJoiner(" ")
                .add("DELETE")
                .add("FROM").add(tableName);
        String whereClause = whereClause(conditionMap);
        if (!whereClause.isEmpty()) {
            query.add(whereClause);
        }
        return query.toString();
    }

    public String buildQuery(long id) {
        return buildQuery(Map.of(primaryKeyColumnName, id));
    }

    private String whereClause(Map<String, Object> conditionMap) {
        return WhereClause.from(conditionMap, allColumnNames).toQuery();
    }
}
