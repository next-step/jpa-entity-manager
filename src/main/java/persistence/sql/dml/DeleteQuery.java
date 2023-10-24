package persistence.sql.dml;

import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

public class DeleteQuery {

    private static final String DEFAULT_DELETE_QUERY = "DELETE FROM %s";

    private final TableName tableName;
    private final Columns columns;
    private final Object arg;

    private DeleteQuery(TableName tableName, Columns columns, Object arg) {
        this.tableName = tableName;
        this.columns = columns;
        this.arg = arg;
    }

    public static String create(TableName tableName, Columns columns, Object arg) {
        return new DeleteQuery(tableName, columns, arg).combine();
    }

    private String combine() {
        return String.join(" ", getTableQuery(), getCondition());
    }

    private String getTableQuery() {
        return String.format(DEFAULT_DELETE_QUERY, tableName.getValue());
    }

    private String getCondition() {
        return ConditionBuilder.getCondition(columns.getIdName(), arg);
    }
}
