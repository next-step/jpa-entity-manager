package database.sql.dml;

import database.sql.Util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static database.sql.Util.quote;

public class WhereClause {
    private final Map<String, Object> conditionMap;
    private final List<String> allColumnNames;

    public WhereClause(Map<String, Object> conditionMap, List<String> allColumnNames) {
        this.conditionMap = conditionMap;
        this.allColumnNames = allColumnNames;

        checkConditionColumns(conditionMap, allColumnNames);
    }

    private void checkConditionColumns(Map<String, Object> conditionMap, List<String> allColumnNames) {
        for (String inputColumnName : conditionMap.keySet()) {
            if (!allColumnNames.contains(inputColumnName)) {
                throw new RuntimeException("Invalid query: " + inputColumnName);
            }
        }
    }

    public String toQuery() {
        if (conditionMap.isEmpty()) {
            return "1";
        }

        return allColumnNames.stream()
                .filter(conditionMap::containsKey)
                .map(columnName -> columnAndValue(columnName, conditionMap.get(columnName)))
                .collect(Collectors.joining(" AND "));
    }

    private static String columnAndValue(String columnName, Object value) {
        // 리스트로 들어왔을 땐 IN 쿼리로
        if (value instanceof List) {
            return String.format("%s IN %s", columnName, inClause((Collection<?>) value));
        } else {
            return String.format("%s = %s", columnName, quote(value));
        }
    }

    private static String inClause(Collection<?> value) {
        return value.stream()
                .map(Util::quote)
                .collect(Collectors.joining(", ", "(", ")"));
    }
}
