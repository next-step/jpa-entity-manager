package database.sql.dml;

import database.sql.util.EntityMetadata;
import persistence.entity.context.PrimaryKeyMissingException;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static database.sql.Util.quote;

public class InsertQueryBuilder {
    private final String tableName;
    private final String primaryKeyColumnName;
    private final List<String> generalColumnNames;
    private final boolean hasAutoIncrementPrimaryKey;

    // XXX: private 함수들 정리 필요. 비슷한 코드가 두 벌씩 있음
    public InsertQueryBuilder(EntityMetadata entityMetadata) {
        tableName = entityMetadata.getTableName();
        primaryKeyColumnName = entityMetadata.getPrimaryKeyColumnName();
        generalColumnNames = entityMetadata.getGeneralColumnNames();
        hasAutoIncrementPrimaryKey = entityMetadata.hasAutoIncrementPrimaryKey();
    }

    public InsertQueryBuilder(Class<?> entityClass) {
        this(new EntityMetadata(entityClass));
    }

    public String buildQuery(Map<String, Object> valueMap) {
        checkGenerationStrategy(null);

        return buildQuery(
                columnClauses(valueMap),
                valueClauses(valueMap));
    }

    public String buildQuery(Long id, Map<String, Object> valueMap) {
        checkGenerationStrategy(id);

        if (id == null) {
            return buildQuery(valueMap);
        }

        return buildQuery(columnClauses(primaryKeyColumnName, valueMap), valueClauses(id, valueMap));
    }

    // XXX Persister insert() 로 옮기기
    private void checkGenerationStrategy(Long id) {
        if (!hasAutoIncrementPrimaryKey && id == null) {
            throw new PrimaryKeyMissingException();
        }
    }

    private String buildQuery(String columns, String values) {
        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);
    }

    private List<String> columns(Map<String, Object> valueMap) {
        return generalColumnNames.stream()
                .filter(valueMap::containsKey)
                .collect(Collectors.toList());
    }

    private String columnClauses(Map<String, Object> valueMap) {
        StringJoiner joiner = new StringJoiner(", ");
        columns(valueMap).forEach(joiner::add);
        return joiner.toString();
    }

    private String columnClauses(String primaryKeyColumnName, Map<String, Object> valueMap) {
        StringJoiner joiner = new StringJoiner(", ");
        joiner.add(primaryKeyColumnName);
        columns(valueMap).forEach(joiner::add);
        return joiner.toString();
    }

    private String valueClauses(Map<String, Object> valueMap) {
        StringJoiner joiner = new StringJoiner(", ");
        columns(valueMap).forEach(key -> joiner.add(quote(valueMap.get(key))));
        return joiner.toString();
    }

    private String valueClauses(Long id, Map<String, Object> valueMap) {
        StringJoiner joiner = new StringJoiner(", ");
        joiner.add(quote(id));
        columns(valueMap).forEach(key -> joiner.add(quote(valueMap.get(key))));
        return joiner.toString();
    }
}
