package database.sql.dml.where;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WhereClause {
    private final Map<String, Object> conditionMap;
    private final List<String> allColumnNames;

    private WhereClause(Map<String, Object> conditionMap, List<String> allColumnNames) {
        this.conditionMap = conditionMap;
        this.allColumnNames = allColumnNames;
    }

    public static WhereClause from(Map<String, Object> conditionMap, List<String> allColumnNames) {
        checkColumnNameInCondition(conditionMap, allColumnNames);

        return new WhereClause(conditionMap, allColumnNames);
    }

    private static void checkColumnNameInCondition(Map<String, Object> conditionMap, List<String> allColumnNames) {
        for (String inputColumnName : conditionMap.keySet()) {
            if (!allColumnNames.contains(inputColumnName)) {
                throw new RuntimeException("Invalid query: " + inputColumnName);
            }
        }
    }

    public String toQuery() {
        if (conditionMap.isEmpty()) {
            return FilterExpression.EMPTY;
        }

        return allColumnNames.stream()
                .filter(conditionMap::containsKey)
                .map(columnName -> columnAndValue(columnName, conditionMap.get(columnName)))
                .collect(Collectors.joining(" AND ", "WHERE ", ""));
    }

    private static String columnAndValue(String columnName, Object value) {
        FilterExpression expr = FilterExpression.from(columnName, value);
        return expr.toQuery();
    }
}
