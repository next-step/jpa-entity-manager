package persistence.sql.metadata;

import persistence.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.Arrays;
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
                .map(Column::getConvertedValue)
                .collect(Collectors.joining(","));
    }

    public String buildSetClause() {
        return columns.stream()
                .filter(Column::checkPossibleToBeValue)
                .map(x -> x.getName() + " = " + x.getConvertedValue())
                .collect(Collectors.joining(", "));
    }

    public String buildWhereClause() {
        return columns.stream()
                .filter(Column::isNotTransient)
                .map(x -> x.getName() + " = " + x.getConvertedValue())
                .collect(Collectors.joining(" AND "));
    }

    public String buildWhereWithPKClause() {
        return columns.stream()
                .filter(Column::isPrimaryKey)
                .map(x -> x.getName() + " = " + x.getConvertedValue())
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public Column getId() {
        return columns.stream()
                .filter(Column::isPrimaryKey)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public static Columns convertEntityToColumnList(Object entity) {
        if(entity == null) {
            return null;
        }

        Field[] fields = entity.getClass().getDeclaredFields();

        return new Columns(
                Arrays.stream(fields)
                .map(x -> {
                    x.setAccessible(true);

                    try {
                        return new Column(x, x.get(entity));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList())
        );
    }
}
