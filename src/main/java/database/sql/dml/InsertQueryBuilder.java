package database.sql.dml;

import database.sql.util.EntityMetadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static database.sql.Util.quote;

public class InsertQueryBuilder {
    private final String tableName;
    private final List<String> generalColumnNames;

    public InsertQueryBuilder(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);
        this.tableName = entityMetadata.getTableName();
        this.generalColumnNames = entityMetadata.getGeneralColumnNames();
    }

    public String buildQuery(Map<String, Object> valueMap) {
        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                             tableName,
                             String.join(", ", columnClauses(valueMap)),
                             valueClauses(valueMap));
    }

    public String buildQuery(Object entity) {
        return buildQuery(columnValues(entity));
    }

    private List<String> columnClauses(Map<String, Object> valueMap) {
        return generalColumnNames.stream()
                .filter(valueMap::containsKey)
                .collect(Collectors.toList());
    }

    private String valueClauses(Map<String, Object> valueMap) {
        return columnClauses(valueMap).stream()
                .map(columnName -> quote(valueMap.get(columnName)))
                .collect(Collectors.joining(", "));
    }

    private Map<String, Object> columnValues(Object entity) {
        return new ColumnValueMap(entity).getColumnValueMap();
    }
}
