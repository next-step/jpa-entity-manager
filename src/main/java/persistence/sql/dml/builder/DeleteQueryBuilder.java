package persistence.sql.dml.builder;

import persistence.sql.ColumnUtils;
import persistence.sql.dml.model.DMLColumn;
import persistence.sql.dml.model.Value;
import persistence.sql.model.Table;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DeleteQueryBuilder {

    private static final String DELETE_QUERY_FORMAT = "DELETE FROM %s WHERE %s;";

    private final Table table;
    private final DMLColumn column;
    private final Value value;

    public DeleteQueryBuilder(Object entity) {
        this(new Table(entity.getClass()), new DMLColumn(entity), new Value(entity));
    }

    public DeleteQueryBuilder(Table table, DMLColumn column, Value value) {
        this.table = table;
        this.column = column;
        this.value = value;
    }

    public String build() {
        return String.format(
                DELETE_QUERY_FORMAT,
                table.name(),
                createWhereClauseQuery()
        );
    }

    private String createWhereClauseQuery() {
        return Arrays.stream(column.getAllFields())
                .filter(column::includeDatabaseColumn)
                .filter(column::excludeIdColumn)
                .map(field -> ColumnUtils.name(field) + " = " + value.getDatabaseValue(field))
                .collect(Collectors.joining(" AND "));
    }
}
