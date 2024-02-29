package persistence.sql.dml;

import persistence.sql.domain.Column;
import persistence.sql.domain.DataType;
import persistence.sql.domain.Table;
import utils.ValueExtractor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InsertQueryBuilder {
    private static final String INSERT_QUERY_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String CLAUSE_DELIMITER = ", ";

    private static class InstanceHolder {
        private static final InsertQueryBuilder INSTANCE = new InsertQueryBuilder();
    }

    public static InsertQueryBuilder getInstance() {
        return InstanceHolder.INSTANCE;
    }


    public String build(Object object) {
        Table table = Table.from(object.getClass());
        List<Column> columns = table.getColumns();
        Map<String, String> clause = getClause(object, columns);
        String columnsClause = String.join(CLAUSE_DELIMITER, clause.keySet());
        String valuesClause = String.join(CLAUSE_DELIMITER, clause.values());
        return String.format(INSERT_QUERY_TEMPLATE, table.getName(), columnsClause, valuesClause);
    }

    private Map<String, String> getClause(Object object, List<Column> columns) {
        return columns.stream()
                .filter(column -> !column.isAutoIncrementId())
                .peek(column -> checkNullableValue(object, column))
                .collect(Collectors.toMap(Column::getName, column -> getDmlValue(object, column),
                        (existingValue, newValue) -> existingValue, LinkedHashMap::new));
    }

    private void checkNullableValue(Object object, Column column) {
        if (!column.isNullable() && (ValueExtractor.extract(object, column) == null)) {
            throw new IllegalArgumentException("Not nullable column value is null");
        }
    }

    private String getDmlValue(Object object, Column column) {
        Object value = ValueExtractor.extract(object, column);
        DataType columnType = column.getType();
        if (columnType.isVarchar()) {
            return String.format("'%s'", value);
        }
        return value.toString();
    }
}
