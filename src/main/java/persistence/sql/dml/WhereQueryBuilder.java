package persistence.sql.dml;

import persistence.sql.QueryBuilder;
import persistence.sql.ddl.domain.Column;
import persistence.sql.ddl.domain.Columns;
import persistence.sql.dml.domain.Value;
import persistence.sql.dml.domain.Values;

import java.util.List;
import java.util.stream.Collectors;

public class WhereQueryBuilder implements QueryBuilder {

    private static final String EMPTY_STRING = "";

    private static final String WHERE_CLAUSE = " WHERE %s";

    private final Values values;

    public WhereQueryBuilder(Object entity) {
        Columns columns = new Columns(entity.getClass());
        columns.getPrimaryKeyColumn();
        Value primaryKeyColumnValue = new Value(columns.getPrimaryKeyColumn(), entity);
        this.values = new Values(primaryKeyColumnValue);
    }

    public WhereQueryBuilder(Column pkColumn, Object id) {
        Value primaryKeyColumnValue = new Value(pkColumn, pkColumn.getField().getType(), id);
        this.values = new Values(primaryKeyColumnValue);
    }

    public WhereQueryBuilder(Class<?> clazz, List<String> whereColumnNames, List<Object> whereValues) {
        validate(whereColumnNames, whereValues);
        this.values = new Values(clazz, whereColumnNames, whereValues);
    }

    private void validate(List<String> whereColumnNames, List<Object> whereValues) {
        if (whereColumnNames.size() != whereValues.size()) {
            throw new IllegalArgumentException("The number of columns and values corresponding to the condition statement do not match.");
        }
    }

    @Override
    public String build() {
        String whereClause = generateWhereClause();
        if (whereClause.isEmpty()) {
            return EMPTY_STRING;
        }
        return String.format(WHERE_CLAUSE, whereClause);
    }

    private String generateWhereClause() {
        return values.getValues().stream()
                .map(value -> value.getColumnName() + " = " + value.getValue())
                .collect(Collectors.joining(" AND "));
    }

}
