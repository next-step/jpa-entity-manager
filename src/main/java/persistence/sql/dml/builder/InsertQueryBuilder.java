package persistence.sql.dml.builder;

import persistence.sql.dml.model.DMLColumn;
import persistence.sql.dml.model.Value;
import persistence.sql.model.Table;

public class InsertQueryBuilder {

    private static final String INSERT_QUERY_FORMAT = "INSERT INTO %s (%s) VALUES (%s);";

    private final Table table;
    private final DMLColumn column;
    private final Value value;

    public InsertQueryBuilder(Object entity) {
        this(new Table(entity.getClass()), new DMLColumn(entity), new Value(entity));
    }

    public InsertQueryBuilder(Table table, DMLColumn column, Value value) {
        this.table = table;
        this.column = column;
        this.value = value;
    }

    public String build() {
        return String.format(
                INSERT_QUERY_FORMAT,
                table.name(),
                column.getAllColumnClause(),
                value.getValueClause()
        );
    }
}
