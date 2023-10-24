package persistence.sql.dml;

import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

public class QueryDml {

    public static String insert(TableName tableName, Columns columns, Values values) {
        return InsertQuery.create(tableName, columns, values);
    }

    public static String select(String methodName, TableName tableName, Columns columns, Object... args) {
        return SelectQuery.create(methodName, tableName, columns, args);
    }

    public static String delete(TableName tableName, Columns columns, Object args) {
        return DeleteQuery.create(tableName, columns, args);
    }

    public static String update(Values values, TableName tableName, Columns columns, Object arg) {
        return UpdateQuery.create(values, tableName, columns, arg);
    }
}
