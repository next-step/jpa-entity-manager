package persistence.sql.dml;

import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

public final class Query {
    private static final Query INSTANCE = new Query();

    private final SelectQuery selectQuery;
    private final InsertQuery insertQuery;
    private final UpdateQuery updateQuery;
    private final DeleteQuery deleteQuery;

    private Query() {
        this.selectQuery = new SelectQuery();
        this.insertQuery = new InsertQuery();
        this.updateQuery = new UpdateQuery();
        this.deleteQuery = new DeleteQuery();
    }

    public static Query getInstance() {
        return INSTANCE;
    }

    public String select(String methodName, TableName tableName, Columns columns, Object... args) {
        return INSTANCE.selectQuery.get(methodName, tableName, columns, args);
    }

    public String insert(TableName tableName, Columns columns, Values values) {
        return INSTANCE.insertQuery.get(tableName, columns, values);
    }

    public String update(Values values, TableName tableName, Columns columns, Object args) {
        return INSTANCE.updateQuery.get(values, tableName, columns, args);
    }

    public String delete(TableName tableName, Columns columns, Object arg) {
        return INSTANCE.deleteQuery.get(tableName, columns, arg);
    }
}
