package persistence.sql.metadata;

import persistence.dialect.Dialect;

import java.util.List;
import java.util.stream.Collectors;

public class Columns {
    private final List<Column> columns;

    public Columns(List<Column> columns) {
        this.columns = columns;
    }

    public String buildColumnsWithConstraint(Dialect dialect) {
        return columns.stream()
                .filter(Column::isNotTransient)
                .map(x -> x.buildColumnsWithConstraint(dialect))
                .collect(Collectors.joining(", "));
    }

    public String buildColumnsClause() {
        return columns.stream()
                .filter(Column::checkPossibleToBeValue)
                .map(Column::getName)
                .collect(Collectors.joining(", "));
    }

    public String buildValueClause() {
        return columns.stream()
                .filter(Column::checkPossibleToBeValue)
                .map(Column::getValue)
                .collect(Collectors.joining(","));
    }

    public String buildSetClause() {
        return columns.stream()
                .filter(Column::checkPossibleToBeValue)
                .map(x -> x.getName() + " = " + x.getValue())
                .collect(Collectors.joining(", "));
    }

    public String buildWhereClause() {
        return columns.stream()
                .filter(Column::isNotTransient)
                .map(x -> x.getName() + " = " + x.getValue())
                .collect(Collectors.joining(" AND "));
    }

    public String buildWhereWithPKClause() {
        return columns.stream()
                .filter(Column::isPrimaryKey)
                .map(x -> x.getName() + " = " + x.getValue())
                .findFirst().get();
    }

    public Column getPrimaryKey() {
        return columns.stream()
                .filter(Column::isPrimaryKey)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
