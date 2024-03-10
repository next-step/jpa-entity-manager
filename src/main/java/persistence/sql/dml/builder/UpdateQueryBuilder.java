package persistence.sql.dml.builder;

import persistence.sql.ColumnUtils;
import persistence.sql.dml.model.DMLColumn;
import persistence.sql.dml.model.Value;
import persistence.sql.model.Table;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    private static final String UPDATE_QUERY_FORMAT = "UPDATE %s SET %s WHERE %s = %s;";

    private final Table table;
    private final DMLColumn column;
    private final Value value;

    public UpdateQueryBuilder(Object entity) {
        this(new Table(entity.getClass()), new DMLColumn(entity), new Value(entity));
    }

    public UpdateQueryBuilder(Table table, DMLColumn column, Value value) {
        this.table = table;
        this.column = column;
        this.value = value;
    }

    public String build(Object id) {
        return String.format(
                UPDATE_QUERY_FORMAT,
                table.name(),
                createSetClauseQuery(),
                column.getIdColumnName(),
                id
        );
    }

    private String createSetClauseQuery() {
        return Arrays.stream(column.getAllFields())
                .filter(column::includeDatabaseColumn)
                .filter(column::excludeIdColumn)
                .map(field -> ColumnUtils.name(field) + " = " + value.getDatabaseValue(field))
                .collect(Collectors.joining(", "));
    }
}
