package persistence.clause;

import persistence.metadata.ColumnName;
import persistence.metadata.WhereCondition;

import java.util.List;
import java.util.stream.Collectors;

public class QueryClause {
    private static final String WHERE = "where";
    private static final String AND = "and";
    private static final String AND_COMMA = ", ";

    public static String columnClause(List<ColumnName> columnNames) {
        return columnNames.stream()
                .map(ColumnName::name)
                .collect(Collectors.joining(AND_COMMA));
    }

    public static String whereClause(List<WhereCondition> whereConditions) {
        return " " +
                WHERE +
                whereConditions.stream()
                        .map(condition -> condition.columnName() + " " + condition.operator() + " " + condition.value())
                        .collect(Collectors.joining(AND, " ", ""));
    }
}
